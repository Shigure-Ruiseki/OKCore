package ruiseki.okcore.data;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.datastructure.Resource;
import ruiseki.okcore.datastructure.ServerGameExecutor;
import ruiseki.okcore.event.data.AddReloadListenerEvent;

public class DataLoader {

    private static final ServerGameExecutor SERVER_EXECUTOR = new ServerGameExecutor();
    private static boolean isExecutorRegistered = false;

    private static final Map<String, ?> EMPTY_ENV = Collections.emptyMap();

    public static void loadAllDataAtServerStart(MinecraftServer server) {
        if (server == null) return;

        if (!isExecutorRegistered) {
            FMLCommonHandler.instance()
                .bus()
                .register(SERVER_EXECUTOR);
            isExecutorRegistered = true;
        }

        OKCore.okLog(Level.INFO, "DataLoader: Server started. Initializing full data reload...");

        SimpleDataManager dataManager = new SimpleDataManager();

        List<FileSystem> openedFileSystems = new ArrayList<>();

        scanModJars(dataManager, openedFileSystems);
        scanWorldDatapacks(server, dataManager);

        AddReloadListenerEvent event = new AddReloadListenerEvent();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
        List<PreparableReloadListener> listeners = event.getListeners();

        Executor backgroundExecutor = ForkJoinPool.commonPool();

        OKCore.okLog(Level.INFO, "DataLoader: Applying {} reload listeners...", listeners.size());

        CompletableFuture<Void> reloadFuture = CompletableFuture.completedFuture(null);
        SimplePreparationBarrier barrier = new SimplePreparationBarrier();

        for (PreparableReloadListener listener : listeners) {
            reloadFuture = reloadFuture
                .thenCompose(v -> listener.reload(barrier, dataManager, backgroundExecutor, SERVER_EXECUTOR));
        }

        reloadFuture.whenComplete((v, ex) -> {
            for (FileSystem fs : openedFileSystems) {
                try {
                    fs.close();
                } catch (IOException e) {
                    OKCore.okLog(Level.ERROR, "Failed to close FileSystem on reload complete", e);
                }
            }

            if (ex != null) {
                OKCore.okLog(Level.ERROR, "DataLoader: Error occurred during resource reload pipeline!", ex);
            } else {
                OKCore.okLog(Level.INFO, "DataLoader: All data packs and mod resources successfully reloaded.");
            }
        });
    }

    private static void scanModJars(SimpleDataManager dataManager, List<FileSystem> openedFileSystems) {
        for (ModContainer mod : Loader.instance()
            .getModList()) {
            String modId = mod.getModId()
                .toLowerCase();
            File modSource = mod.getSource();

            if (modSource == null || !modSource.isFile()
                || !modSource.getName()
                    .endsWith(".jar")) {
                continue;
            }

            URI uri = URI.create("jar:" + modSource.toURI());
            FileSystem fileSystem = null;

            try {
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, EMPTY_ENV);
                    openedFileSystems.add(fileSystem);
                }

                Path dataPath = fileSystem.getPath("/data");
                if (!Files.exists(dataPath)) continue;

                Path packMcMeta = fileSystem.getPath("/pack.mcmeta");
                if (!Files.exists(packMcMeta)) {
                    OKCore.okLog(
                        Level.WARN,
                        "Mod '{}' contains a 'data' folder but is missing 'pack.mcmeta'. Skipping.",
                        modId);
                    continue;
                }

                Path rootPath = fileSystem.getPath("/");
                try (Stream<Path> stream = Files.walk(dataPath, FileVisitOption.FOLLOW_LINKS)) {
                    stream.filter(p -> !Files.isDirectory(p))
                        .forEach(p -> processStream(p, rootPath, dataManager));
                }
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Critical error while scanning JAR data for mod: " + modId, e);
            }
        }
    }

    private static void scanWorldDatapacks(MinecraftServer server, SimpleDataManager dataManager) {
        String folderName = server.getFolderName();
        File realWorldDir;

        if (server.isDedicatedServer()) {
            realWorldDir = new File(folderName);
        } else {
            File savesDir = FMLCommonHandler.instance()
                .getSavesDirectory();
            realWorldDir = new File(savesDir, folderName);
        }

        File datapacksDir = new File(realWorldDir, "datapacks");
        if (!datapacksDir.exists() && !datapacksDir.mkdirs()) {
            return;
        }

        File[] packs = datapacksDir.listFiles();
        if (packs == null) return;

        for (File packDir : packs) {
            if (!packDir.isDirectory()) continue;

            File dataDir = new File(packDir, "data");
            File packMcMeta = new File(packDir, "pack.mcmeta");

            if (!dataDir.exists() || !dataDir.isDirectory() || !packMcMeta.exists()) {
                continue;
            }

            try (Stream<Path> stream = Files.walk(packDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
                final Path rootPath = packDir.toPath();
                stream.filter(p -> !Files.isDirectory(p))
                    .forEach(p -> processStream(p, rootPath, dataManager));
            } catch (Exception e) {
                OKCore
                    .okLog(Level.ERROR, "DataLoader: Critical error while scanning datapack: " + packDir.getName(), e);
            }
        }
    }

    private static void processStream(Path p, Path rootPath, SimpleDataManager dataManager) {
        try {
            String relativeStr = rootPath.relativize(p)
                .toString()
                .replace('\\', '/');

            if (relativeStr.startsWith("/")) {
                relativeStr = relativeStr.substring(1);
            }

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
