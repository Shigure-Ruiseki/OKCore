package ruiseki.okcore.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import ruiseki.okcore.OKCore;

public class DataLoader {

    public static final Pattern DYNAMIC_DATA_PATTERN = Pattern
        .compile("^data/([^/]+)/([^/]+)/(?:(.+)/)?([^/]+\\.json)$");

    private static final Map<String, ?> EMPTY_ENV = Collections.emptyMap();

    public static void loadAllData() {
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
            boolean shouldClose = false;

            try {
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fileSystem = FileSystems.newFileSystem(uri, EMPTY_ENV);
                    shouldClose = true;
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
                        .filter(
                            p -> p.toString()
                                .endsWith(".json"))
                        .forEach(p -> processStream(p, rootPath, false));
                }
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Critical error while scanning JAR data for mod: " + modId, e);
            } finally {
                if (shouldClose && fileSystem != null) {
                    try {
                        fileSystem.close();
                    } catch (IOException e) {
                        OKCore.okLog(Level.ERROR, "Failed to close FileSystem for mod: " + modId, e);
                    }
                }
            }
        }
    }

    public static void loadWorldData(File worldDir) {
        if (worldDir == null) {
            OKCore.okLog(Level.ERROR, "Cannot load world data because 'worldDir' is NULL!");
            return;
        }

        File datapacksDir;
        if (worldDir.getAbsolutePath()
            .contains(File.separator + "saves" + File.separator)) {
            datapacksDir = new File(worldDir, "datapacks");
        } else {
            File runClientDir = worldDir.getParentFile();
            File savesDir = new File(runClientDir, "saves");
            File realWorldDir = new File(savesDir, worldDir.getName());
            datapacksDir = new File(realWorldDir, "datapacks");
        }

        if (!datapacksDir.exists()) {
            OKCore.okLog(
                Level.WARN,
                "Datapacks loading ABORTED: The directory does not exist at path: '{}'",
                datapacksDir.getAbsolutePath());
            return;
        }

        File[] packs = datapacksDir.listFiles();
        if (packs == null) return;

        OKCore.okLog(Level.INFO, "Scanning world data in: {}", datapacksDir.getAbsolutePath());

        for (File packDir : packs) {
            if (!packDir.isDirectory()) continue;

            File dataDir = new File(packDir, "data");
            if (!dataDir.exists() || !dataDir.isDirectory()) {
                continue;
            }

            File packMcMeta = new File(packDir, "pack.mcmeta");
            if (!packMcMeta.exists()) {
                OKCore.okLog(
                    Level.WARN,
                    "Datapack '{}' contains a 'data' folder but is missing 'pack.mcmeta'. Skipping.",
                    packDir.getName());
                continue;
            }

            try (Stream<Path> stream = Files.walk(packDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
                final Path rootPath = datapacksDir.toPath();
                stream.filter(p -> !Files.isDirectory(p))
                    .filter(
                        p -> p.toString()
                            .endsWith(".json"))
                    .forEach(p -> processStream(p, rootPath, true));
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Critical error while scanning datapack: " + packDir.getName(), e);
            }
        }
    }

    private static void processStream(Path p, Path rootPath, boolean isWorldData) {
        try {
            String relativeStr = rootPath.relativize(p)
                .toString()
                .replace('\\', '/');

            if (relativeStr.startsWith("/")) {
                relativeStr = relativeStr.substring(1);
            }

            if (isWorldData) {
                int dataIdx = relativeStr.indexOf("data/");
                if (dataIdx == -1) return;
                relativeStr = relativeStr.substring(dataIdx);
            }

            Matcher matcher = DYNAMIC_DATA_PATTERN.matcher(relativeStr);
            if (!matcher.matches()) return;

            String namespace = matcher.group(1);
            String folder = matcher.group(2);
            String[] subPaths = matcher.group(3) != null ? matcher.group(3)
                .split("/") : new String[0];
            String fileName = matcher.group(4);

            String cleanName = fileName.substring(0, fileName.lastIndexOf('.'));
            String pathPrefix = matcher.group(3) != null ? matcher.group(3) + "/" : "";

            ResourceLocation generatedId = new ResourceLocation(namespace, folder + "/" + pathPrefix + cleanName);

            try (InputStream is = Files.newInputStream(p)) {
                if (isWorldData) {
                    DataHandler.handleWorld(generatedId, namespace, folder, subPaths, fileName, is);
                } else {
                    DataHandler.handleMod(generatedId, namespace, folder, subPaths, fileName, is);
                }
            } catch (IOException e) {
                OKCore.okLog(Level.ERROR, "Failed to read data stream for: " + relativeStr, e);
            }
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Error processing data stream at: " + p, e);
        }
    }
}
