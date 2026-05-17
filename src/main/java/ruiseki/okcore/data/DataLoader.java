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

            try (InputStream is = Files.newInputStream(p)) {
                DataHandler.handle(namespace, folder, subPaths, fixedFileName, is);
            } catch (IOException e) {
                OKCore.okLog(Level.ERROR, "Failed to read data stream for: " + fullMatchPath, e);
            }
        } catch (Exception e) {
            OKCore.okLog(Level.ERROR, "Error processing specific data stream at: " + p, e);
        }
    }
}
