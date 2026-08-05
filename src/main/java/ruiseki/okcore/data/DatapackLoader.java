package ruiseki.okcore.data;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.Resource;
import ruiseki.okcore.event.data.AddReloadListenerEvent;

public class DatapackLoader {

    private static final Map<String, ?> EMPTY_ENV = Collections.emptyMap();

    public static CompletableFuture<Void> loadAllData(MinecraftServer server) {
        if (server == null) return CompletableFuture.completedFuture(null);
        long startTime = System.currentTimeMillis();
        OKCore.okLog(Level.INFO, "DataLoader: Starting parallel data reload pipeline...");

        String folderName = server.getFolderName();
        File realWorldDir = server.isDedicatedServer() ? new File(folderName)
            : new File(
                FMLCommonHandler.instance()
                    .getSavesDirectory(),
                folderName);

        DatapackManager datapackManager = DatapackManager.INSTANCE;
        datapackManager.clear();
        datapackManager.loadDisabledPacksConfig(realWorldDir);

        ConcurrentLinkedQueue<FileSystem> openedFileSystems = new ConcurrentLinkedQueue<>();
        Executor ioExecutor = ForkJoinPool.commonPool();

        ConcurrentLinkedQueue<Runnable> startupTaskQueue = new ConcurrentLinkedQueue<>();
        Executor startupAppExecutor = startupTaskQueue::add;

        return CompletableFuture.runAsync(() -> {
            try {
                CompletableFuture<Void> scanModJarsFuture = CompletableFuture
                    .runAsync(() -> scanModJars(datapackManager, openedFileSystems), ioExecutor);

                CompletableFuture<Void> scanDatapacksFuture = CompletableFuture
                    .runAsync(() -> scanWorldDatapacks(realWorldDir, datapackManager), ioExecutor);

                CompletableFuture<Void> scanPhaseFuture = CompletableFuture
                    .allOf(scanModJarsFuture, scanDatapacksFuture);

                scanPhaseFuture.join();

                AddReloadListenerEvent event = new AddReloadListenerEvent();
                MinecraftForge.EVENT_BUS.post(event);

                List<PreparableReloadListener> listeners = event.getListeners();

                OKCore.okLog(
                    Level.INFO,
                    "DataLoader: Core Scan done in {} ms. " + "Initiating parallel preparation for {} listeners...",
                    (System.currentTimeMillis() - startTime),
                    listeners.size());

                SimplePreparationBarrier barrier = new SimplePreparationBarrier();

                List<CompletableFuture<Void>> listenerFutures = new ArrayList<>(listeners.size());

                for (PreparableReloadListener listener : listeners) {
                    listenerFutures.add(listener.reload(barrier, datapackManager, ioExecutor, startupAppExecutor));
                }

                CompletableFuture<Void> allPreparationsFuture = CompletableFuture
                    .allOf(listenerFutures.toArray(new CompletableFuture[0]));

                while (!allPreparationsFuture.isDone() || !startupTaskQueue.isEmpty()) {

                    Runnable task = startupTaskQueue.poll();

                    if (task != null) {
                        try {
                            task.run();
                        } catch (Throwable t) {
                            OKCore.okLog(Level.ERROR, "DataLoader: Error while executing startup task", t);
                        }
                    } else {
                        Thread.yield();
                    }
                }

                allPreparationsFuture.join();

                Runnable task;

                while ((task = startupTaskQueue.poll()) != null) {
                    try {
                        task.run();
                    } catch (Throwable t) {
                        OKCore.okLog(Level.ERROR, "DataLoader: Error while executing final startup task", t);
                    }
                }

                OKCore.okLog(
                    Level.INFO,
                    "DataLoader: All data successfully reloaded " + "in Total: {} ms.",
                    (System.currentTimeMillis() - startTime));

            } catch (Throwable t) {
                OKCore
                    .okLog(Level.ERROR, "DataLoader: Critical error occurred during " + "resource reload pipeline!", t);

                throw new RuntimeException("Data reload failed", t);

            } finally {
                for (FileSystem fs : openedFileSystems) {
                    try {
                        fs.close();
                    } catch (IOException e) {
                        OKCore.okLog(Level.ERROR, "Failed to close FileSystem on reload complete", e);
                    }
                }
            }
        }, ioExecutor);
    }

    private static void scanModJars(DatapackManager datapackManager,
        ConcurrentLinkedQueue<FileSystem> openedFileSystems) {
        Set<File> uniqueJarFiles = Loader.instance()
            .getModList()
            .stream()
            .map(ModContainer::getSource)
            .filter(Objects::nonNull)
            .filter(File::isFile)
            .filter(
                file -> file.getName()
                    .endsWith(".jar"))
            .collect(Collectors.toSet());

        uniqueJarFiles.parallelStream()
            .forEach(modSource -> {
                URI uri = URI.create("jar:" + modSource.toURI());
                FileSystem fileSystem = null;

                try {
                    fileSystem = obtainFileSystem(uri, openedFileSystems);

                    if (fileSystem == null) {
                        return;
                    }

                    Path dataPath = fileSystem.getPath("/data");

                    if (!Files.exists(dataPath)) {
                        return;
                    }

                    Path packMcMeta = fileSystem.getPath("/pack.mcmeta");
                    if (!Files.exists(packMcMeta)) {
                        OKCore.okLog(
                            Level.WARN,
                            "JAR '{}' contains a 'data' folder " + "but is missing 'pack.mcmeta'. Skipping.",
                            modSource.getName());
                        return;
                    }

                    Path rootPath = fileSystem.getPath("/");
                    SimpleDataManager modDataManager = new SimpleDataManager();

                    try (Stream<Path> stream = Files.walk(dataPath, FileVisitOption.FOLLOW_LINKS)) {
                        stream.filter(p -> !Files.isDirectory(p))
                            .forEach(p -> processStream(p, rootPath, modDataManager));
                    }

                    datapackManager.registerDatapack(modSource.getName(), modDataManager, true);

                } catch (Exception e) {
                    OKCore.okLog(Level.ERROR, "Critical error while scanning JAR data for: " + modSource.getName(), e);
                }
            });
    }

    private static FileSystem obtainFileSystem(URI uri, ConcurrentLinkedQueue<FileSystem> openedFileSystems) {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            try {
                FileSystem fs = FileSystems.newFileSystem(uri, EMPTY_ENV);
                openedFileSystems.add(fs);
                return fs;
            } catch (FileSystemAlreadyExistsException ex) {
                try {
                    return FileSystems.getFileSystem(uri);
                } catch (Exception innerEx) {
                    OKCore.okLog(Level.ERROR, "Failed to obtain existing FileSystem for URI: " + uri, innerEx);
                    return null;
                }
            } catch (IOException ioEx) {
                OKCore.okLog(Level.ERROR, "Failed to create FileSystem for URI: " + uri, ioEx);
                return null;
            }
        }
    }

    private static void scanWorldDatapacks(File realWorldDir, DatapackManager datapackManager) {
        File datapacksDir = new File(realWorldDir, "datapacks");
        if (!datapacksDir.exists() && !datapacksDir.mkdirs()) return;

        File[] packs = datapacksDir.listFiles();
        if (packs == null) return;

        Stream.of(packs)
            .parallel()
            .forEach(packDir -> {
                if (!packDir.isDirectory()) return;

                File dataDir = new File(packDir, "data");
                File packMcMeta = new File(packDir, "pack.mcmeta");

                if (!dataDir.exists() || !dataDir.isDirectory() || !packMcMeta.exists()) return;

                try {
                    SimpleDataManager packDataManager = new SimpleDataManager();

                    try (Stream<Path> stream = Files.walk(packDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
                        final Path rootPath = packDir.toPath();
                        stream.filter(p -> !Files.isDirectory(p))
                            .forEach(p -> processStream(p, rootPath, packDataManager));
                    }

                    datapackManager.registerDatapack(packDir.getName(), packDataManager, false);

                } catch (Exception e) {
                    OKCore.okLog(
                        Level.ERROR,
                        "DataLoader: Critical error while scanning datapack: " + packDir.getName(),
                        e);
                }
            });
    }

    private static void processStream(Path p, Path rootPath, SimpleDataManager dataManager) {
        try {
            String relativeStr = rootPath.relativize(p)
                .toString()
                .replace('\\', '/');
            if (relativeStr.startsWith("/")) relativeStr = relativeStr.substring(1);

            int dataIdx = relativeStr.indexOf("data/");
            if (dataIdx == -1) return;

            relativeStr = relativeStr.substring(dataIdx);
            String[] segments = relativeStr.split("/");
            if (segments.length < 3) return;

            String namespace = segments[1];
            int prefixLength = "data/".length() + namespace.length() + 1;
            String resourcePath = relativeStr.substring(prefixLength);

            ResourceLocation fileLocation = new ResourceLocation(namespace, resourcePath);
            Resource resource = new Resource(() -> Files.newInputStream(p));
            dataManager.registerResource(namespace, fileLocation, resource);
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Error processing data stream at: " + p, e);
        }
    }
}
