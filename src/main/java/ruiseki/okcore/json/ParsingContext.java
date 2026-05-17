package ruiseki.okcore.json;

import java.io.File;

/**
 * Holds the current context during JSON parsing, such as the file being
 * processed.
 * Uses ThreadLocal to ensure thread safety during potentially parallel parsing.
 */
public class ParsingContext {

    private static final ThreadLocal<File> currentFile = new ThreadLocal<>();
    private static final ThreadLocal<String> currentResourceName = new ThreadLocal<>();

    public static void setCurrentFile(File file) {
        currentFile.set(file);
        if (file != null) {
            currentResourceName.set(file.getName());
        } else {
            currentResourceName.remove();
        }
    }

    public static File getCurrentFile() {
        return currentFile.get();
    }

    public static void setCurrentFileName(String resourceName) {
        currentResourceName.set(resourceName);
        currentFile.remove();
    }

    public static String getCurrentFileName() {
        String name = currentResourceName.get();
        if (name != null) {
            return name;
        }

        File file = currentFile.get();
        return file != null ? file.getName() : "unknown";
    }

    public static void clear() {
        currentFile.remove();
        currentResourceName.remove();
    }
}
