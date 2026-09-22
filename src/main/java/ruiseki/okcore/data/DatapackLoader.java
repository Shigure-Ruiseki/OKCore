package ruiseki.okcore.data;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.condition.ConditionContext;
import ruiseki.okcore.data.condition.ICondition;
import ruiseki.okcore.datastructure.Resource;
import ruiseki.okcore.event.data.AddReloadListenerEvent;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.tag.TagManager;

public class DatapackLoader {

    private static final Map<String, ?> EMPTY_ENV = Collections.emptyMap();
    private static final Runnable END_MARKER = () -> {};
    private static final ConcurrentHashMap<URI, Object> FILE_SYSTEM_LOCKS = new ConcurrentHashMap<>();

    public static CompletableFuture<Void> loadAllData(MinecraftServer server) {
        if (server == null) return CompletableFuture.completedFuture(null);
        final long startTime = System.currentTimeMillis();
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

        BlockingQueue<Runnable> startupTaskQueue = new LinkedBlockingQueue<>();
        Executor startupAppExecutor = startupTaskQueue::add;

        // Step 1: Scan Mod JAR
        CompletableFuture<Void> scanModJarsFuture = scanModJars(datapackManager, openedFileSystems, ioExecutor);

        // Step 2: Scan World Datapacks
        CompletableFuture<Void> scanDatapacksFuture = scanWorldDatapacks(
            server,
            realWorldDir,
            datapackManager,
            ioExecutor);

        CompletableFuture<Void> pipelineFuture = CompletableFuture.allOf(scanModJarsFuture, scanDatapacksFuture)
            .thenComposeAsync(
                ignored -> prepareListeners(
                    datapackManager,
                    ioExecutor,
                    startupAppExecutor,
                    startupTaskQueue,
                    startTime),
                ioExecutor);

        CompletableFuture<Void> finalSyncFuture = pipelineFuture.thenRunAsync(() -> {
            RecipeRegistry.syncMCCraftingManager();
            RecipeRegistry.syncMCFurnaceRecipes();
            OKCore.okLog(Level.INFO, "DataLoader: Vanilla crafting & furnace recipes synced.");
        }, startupAppExecutor);

        CompletableFuture<Void> drainFuture = CompletableFuture
            .runAsync(() -> drainStartupTasks(finalSyncFuture, startupTaskQueue), ioExecutor);

        return drainFuture.whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                OKCore.okLog(
                    Level.ERROR,
                    "DataLoader: Critical error occurred during resource reload pipeline!",
                    throwable);
            } else {
                OKCore.okLog(
                    Level.INFO,
                    "DataLoader: All data successfully reloaded in Total: {} ms.",
                    System.currentTimeMillis() - startTime);
            }

            closeFileSystems(openedFileSystems);
        });
    }

    private static CompletableFuture<Void> prepareListeners(DatapackManager datapackManager, Executor ioExecutor,
        Executor startupAppExecutor, BlockingQueue<Runnable> startupTaskQueue, long startTime) {

        SimplePreparationBarrier barrier = new SimplePreparationBarrier();

        OKCore.okLog(
            Level.INFO,
            "DataLoader: Core Scan done in {} ms. Starting phased execution...",
            System.currentTimeMillis() - startTime);

        // Step 3: Load Tag Data
        CompletableFuture<Void> tagFuture = TagManager.getManager()
            .reload(barrier, datapackManager, ioExecutor, startupAppExecutor);

        ICondition.IContext context = new ConditionContext(TagManager.getManager());

        RecipeManager.getManager()
            .setContext(context);

        // Step 4: Load Recipe Data
        CompletableFuture<Void> recipeFuture = tagFuture.thenComposeAsync(
            ignored -> RecipeManager.getManager()
                .reload(barrier, datapackManager, ioExecutor, startupAppExecutor),
            ioExecutor);

        // Step 5: Load External Reload Listeners
        return recipeFuture.thenComposeAsync(ignored -> {
            AddReloadListenerEvent event = new AddReloadListenerEvent();
            MinecraftForge.EVENT_BUS.post(event);

            List<PreparableReloadListener> listeners = event.getListeners();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (PreparableReloadListener listener : listeners) {
                CompletableFuture<Void> f = listener.reload(barrier, datapackManager, ioExecutor, startupAppExecutor);
                if (f != null) futures.add(f);
            }

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        }, ioExecutor);
    }

    private static CompletableFuture<Void> scanModJars(DatapackManager datapackManager,
        ConcurrentLinkedQueue<FileSystem> openedFileSystems, Executor ioExecutor) {

        List<File> modSources = Loader.instance()
            .getModList()
            .stream()
            .map(ModContainer::getSource)
            .filter(Objects::nonNull)
            .filter(File::isFile)
            .filter(
                file -> file.getName()
                    .endsWith(".jar"))
            .distinct()
            .toList();

        if (modSources.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletableFuture<SimpleDataManager>> futures = new ArrayList<>(modSources.size());

        for (File modSource : modSources) {
            futures.add(CompletableFuture.supplyAsync(() -> scanModJar(modSource, openedFileSystems), ioExecutor));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenRunAsync(() -> {

                for (int i = 0; i < futures.size(); i++) {
                    SimpleDataManager dataManager = futures.get(i)
                        .join();

                    if (dataManager == null) {
                        continue;
                    }

                    datapackManager.registerDatapack(
                        modSources.get(i)
                            .getName(),
                        dataManager,
                        true);
                }
            }, ioExecutor);
    }

    private static SimpleDataManager scanModJar(File modSource, ConcurrentLinkedQueue<FileSystem> openedFileSystems) {
        URI uri = URI.create("jar:" + modSource.toURI());

        try {
            FileSystem fileSystem = obtainFileSystem(uri, openedFileSystems);

            if (fileSystem == null) {
                return null;
            }

            Path dataPath = fileSystem.getPath("/data");

            if (!Files.isDirectory(dataPath)) {
                return null;
            }

            Path packMcMeta = fileSystem.getPath("/pack.mcmeta");

            if (!Files.isRegularFile(packMcMeta)) {
                OKCore.okLog(
                    Level.WARN,
                    "JAR '{}' contains a 'data' folder " + "but is missing 'pack.mcmeta'. Skipping.",
                    modSource.getName());

                return null;
            }

            Path rootPath = fileSystem.getPath("/");

            SimpleDataManager dataManager = new SimpleDataManager();

            try (Stream<Path> stream = Files.walk(dataPath)) {

                stream.filter(path -> !Files.isDirectory(path))
                    .forEach(path -> processStream(path, rootPath, dataManager));
            }

            return dataManager;

        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Critical error while scanning JAR data for: " + modSource.getName(), e);

            return null;
        }
    }

    private static FileSystem obtainFileSystem(URI uri, ConcurrentLinkedQueue<FileSystem> openedFileSystems) {
        synchronized (FILE_SYSTEM_LOCKS.computeIfAbsent(uri, k -> new Object())) {
            try {
                return FileSystems.getFileSystem(uri);
            } catch (FileSystemNotFoundException e) {
                try {
                    FileSystem fileSystem = FileSystems.newFileSystem(uri, EMPTY_ENV);
                    openedFileSystems.add(fileSystem);
                    return fileSystem;
                } catch (IOException ioEx) {
                    OKCore.okLog(Level.ERROR, "Failed to create FileSystem for URI: " + uri, ioEx);
                    return null;
                }
            }
        }
    }

    private static CompletableFuture<Void> scanWorldDatapacks(MinecraftServer server, File realWorldDir,
        DatapackManager datapackManager, Executor ioExecutor) {

        List<File> datapackDirsToScan = new ArrayList<>();

        // 1. Default <worldDir>/datapacks
        File worldDatapacksDir = new File(realWorldDir, "datapacks");
        if (worldDatapacksDir.exists() || worldDatapacksDir.mkdirs()) {
            datapackDirsToScan.add(worldDatapacksDir);
        }

        // 2. Single Player or LAN <gameDir>/datapacks
        if (!server.isDedicatedServer()) {
            File gameRootDir = FMLCommonHandler.instance()
                .getSavesDirectory()
                .getParentFile();
            if (gameRootDir != null && gameRootDir.exists()) {
                File rootDatapacksDir = new File(gameRootDir, "datapacks");
                if (rootDatapacksDir.exists() || rootDatapacksDir.mkdirs()) {
                    datapackDirsToScan.add(rootDatapacksDir);
                }
            }
        }

        List<File> validPackDirs = new ArrayList<>();
        for (File datapacksDir : datapackDirsToScan) {
            File[] packs = datapacksDir.listFiles();
            if (packs != null) {
                for (File packDir : packs) {
                    if (packDir.isDirectory()) {
                        validPackDirs.add(packDir);
                    }
                }
            }
        }

        if (validPackDirs.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        List<CompletableFuture<SimpleDataManager>> futures = new ArrayList<>(validPackDirs.size());

        for (File packDir : validPackDirs) {
            futures.add(CompletableFuture.supplyAsync(() -> scanWorldDatapack(packDir), ioExecutor));
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenRunAsync(() -> {

                for (int i = 0; i < futures.size(); i++) {
                    SimpleDataManager dataManager = futures.get(i)
                        .join();

                    if (dataManager == null) {
                        continue;
                    }

                    datapackManager.registerDatapack(
                        validPackDirs.get(i)
                            .getName(),
                        dataManager,
                        false);
                }
            }, ioExecutor);
    }

    private static SimpleDataManager scanWorldDatapack(File packDir) {

        File dataDir = new File(packDir, "data");

        File packMcMeta = new File(packDir, "pack.mcmeta");

        if (!dataDir.isDirectory() || !packMcMeta.isFile()) {

            return null;
        }

        try {
            SimpleDataManager dataManager = new SimpleDataManager();

            Path rootPath = packDir.toPath();

            try (Stream<Path> stream = Files.walk(dataDir.toPath())) {

                stream.filter(path -> !Files.isDirectory(path))
                    .forEach(path -> processStream(path, rootPath, dataManager));
            }

            return dataManager;

        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "DataLoader: Critical error while scanning datapack: " + packDir.getName(), e);

            return null;
        }
    }

    private static void processStream(Path path, Path rootPath, SimpleDataManager dataManager) {

        try {
            String relativePath = rootPath.relativize(path)
                .toString()
                .replace('\\', '/');

            if (!relativePath.startsWith("data/")) {
                return;
            }

            String dataRelative = relativePath.substring(5);

            int namespaceEnd = dataRelative.indexOf('/');

            if (namespaceEnd <= 0 || namespaceEnd == dataRelative.length() - 1) {
                return;
            }

            String namespace = dataRelative.substring(0, namespaceEnd);
            String resourcePath = dataRelative.substring(namespaceEnd + 1);

            ResourceLocation location = new ResourceLocation(namespace, resourcePath);

            Resource resource = new Resource(() -> Files.newInputStream(path));

            dataManager.registerResource(namespace, location, resource);

        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Error processing data stream at: " + path, e);
        }
    }

    private static void drainStartupTasks(CompletableFuture<Void> finalSyncFuture,
        BlockingQueue<Runnable> startupTaskQueue) {

        finalSyncFuture.whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                startupTaskQueue.clear();
            }
            startupTaskQueue.add(END_MARKER);
        });

        try {
            while (true) {
                Runnable task = startupTaskQueue.take();

                if (task == END_MARKER) {
                    break;
                }

                try {
                    task.run();

                } catch (Throwable t) {
                    OKCore.okLog(Level.ERROR, "DataLoader: Error while executing startup task", t);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread()
                .interrupt();

            OKCore.okLog(Level.ERROR, "DataLoader: Startup task executor was interrupted", e);
        }
    }

    private static void closeFileSystems(ConcurrentLinkedQueue<FileSystem> openedFileSystems) {

        FileSystem fileSystem;

        while ((fileSystem = openedFileSystems.poll()) != null) {

            try {
                fileSystem.close();

            } catch (IOException e) {
                OKCore.okLog(Level.ERROR, "Failed to close FileSystem on reload complete", e);
            }
        }
    }
}
