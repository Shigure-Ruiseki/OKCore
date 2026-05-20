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

            if (modSource == null) continue;

            String targetSubPath = "data/" + modId;

            if (modSource.isDirectory()) {
                File dataDir = new File(modSource, targetSubPath);
                if (!dataDir.exists()) continue;

                try (Stream<Path> stream = Files.walk(dataDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
                    final Path rootPath = dataDir.toPath();
                    stream.filter(p -> !Files.isDirectory(p))
                        .forEach(p -> processSingleStream(p, rootPath, modId));
                } catch (Exception e) {
                    OKCore.okLog(Level.ERROR, "Critical error while scanning directory data for mod: " + modId, e);
                }
            } else if (modSource.isFile() && modSource.getName()
                .endsWith(".jar")) {
                    URI uri = URI.create("jar:" + modSource.toURI());

                    try (FileSystem fileSystem = getFileSystemInstance(uri)) {
                        Path rootPath = fileSystem.getPath("/" + targetSubPath);
                        if (!Files.exists(rootPath)) continue;

                        try (Stream<Path> stream = Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)) {
                            stream.filter(p -> !Files.isDirectory(p))
                                .forEach(p -> processSingleStream(p, rootPath, modId));
                        }
                    } catch (Exception e) {
                        OKCore.okLog(Level.ERROR, "Critical error while scanning JAR data for mod: " + modId, e);
                    }
                }
        }
    }

    private static FileSystem getFileSystemInstance(URI uri) throws IOException {
        try {
            return FileSystems.getFileSystem(uri);
        } catch (FileSystemNotFoundException e) {
            return FileSystems.newFileSystem(uri, EMPTY_ENV);
        }
    }

    private static void processSingleStream(Path p, Path finalRootPath, String modId) {
        try {
            String relativeStr = finalRootPath.relativize(p)
                .toString()
                .replace('\\', '/');
            String fullMatchPath = "data/" + modId + "/" + relativeStr;

            Matcher matcher = DYNAMIC_DATA_PATTERN.matcher(fullMatchPath);
            if (!matcher.matches()) return;

            String namespace = matcher.group(1);
            String folder = matcher.group(2);
            String[] subPaths = matcher.group(3) != null ? matcher.group(3)
                .split("/") : new String[0];
            String fileName = matcher.group(4);

            String fixedFileName = fileName.endsWith(".json") ? fileName : fileName + ".json";

            String cleanName = fixedFileName.substring(0, fixedFileName.lastIndexOf('.'));

            String pathPrefix = matcher.group(3) != null ? matcher.group(3) + "/" : "";
            ResourceLocation generatedId = new ResourceLocation(namespace, folder + "/" + pathPrefix + cleanName);

            try (InputStream is = Files.newInputStream(p)) {
                DataHandler.handleMod(generatedId, namespace, folder, subPaths, fixedFileName, is);
            } catch (IOException e) {
                OKCore.okLog(Level.ERROR, "Failed to read data stream for: " + fullMatchPath, e);
            }
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Error processing specific data stream at: " + p, e);
        }
    }

    public static void loadWorldData(File worldDir) {
        File dataDir = new File(worldDir, "okcore");
        if (!dataDir.exists() || !dataDir.isDirectory()) return;

        OKCore.okLog(Level.INFO, "Scanning world data in: {}", dataDir.getAbsolutePath());

        try (Stream<Path> stream = Files.walk(dataDir.toPath(), FileVisitOption.FOLLOW_LINKS)) {
            final Path rootPath = dataDir.toPath();
            stream.filter(p -> !Files.isDirectory(p))
                .filter(
                    p -> p.toString()
                        .endsWith(".json"))
                .forEach(p -> processWorldStream(p, rootPath));
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Critical error while scanning world data directory", e);
        }
    }

    private static void processWorldStream(Path p, Path rootPath) {
        try {
            String relativeStr = rootPath.relativize(p)
                .toString()
                .replace('\\', '/');
            String fullMatchPath = relativeStr.startsWith("data/") ? relativeStr : "data/" + relativeStr;

            Matcher matcher = DYNAMIC_DATA_PATTERN.matcher(fullMatchPath);
            if (!matcher.matches()) {
                OKCore.okLog(Level.WARN, "World data path does not match pattern: {}", fullMatchPath);
                return;
            }

            String namespace = matcher.group(1);
            String folder = matcher.group(2);
            String[] subPaths = matcher.group(3) != null ? matcher.group(3)
                .split("/") : new String[0];
            String fileName = matcher.group(4);

            String cleanName = fileName.substring(0, fileName.lastIndexOf('.'));
            String pathPrefix = matcher.group(3) != null ? matcher.group(3) + "/" : "";
            ResourceLocation id = new ResourceLocation(namespace, folder + "/" + pathPrefix + cleanName);

            try (InputStream is = Files.newInputStream(p)) {
                DataHandler.handleWorld(id, namespace, folder, subPaths, fileName, is);
            } catch (IOException e) {
                OKCore.okLog(Level.ERROR, "Failed to read world data stream: " + fullMatchPath, e);
            }
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Error processing world data stream at: " + p, e);
        }
    }
}
