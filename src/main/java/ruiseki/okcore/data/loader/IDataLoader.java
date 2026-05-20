package ruiseki.okcore.data.loader;

import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

/**
 * Interface for custom data loading logic within OKCore.
 * Classes implementing this interface are responsible for parsing and registering
 * data files (typically JSON) discovered during the data scanning process.
 */
public interface IDataLoader {

    /**
     * Specifies the target directory name this loader handles.
     * <p>
     * For example, if this returns "recipes", the loader will process all files
     * found within the 'recipes' subdirectory of the data structure.
     *
     * @return The target folder name.
     */
    String getTargetFolder();

    /**
     * Processes the data content from the provided input stream.
     *
     * @param id          The unique {@link ResourceLocation} identifying this data entry.
     * @param namespace   The domain/namespace of the data (e.g., "minecraft").
     * @param folder      The base folder name (matches {@link #getTargetFolder()}).
     * @param subPaths    An array of directories representing the path hierarchy below the base folder.
     * @param fileName    The name of the file including its extension (e.g., "example.json").
     * @param inputStream The {@link InputStream} to read the raw data content.
     */
    void process(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream);

    /**
     * Indicates whether this loader should proceed with data registration.
     *
     * @return true if the loader should register data, false to skip. Defaults to true.
     */
    default boolean shouldRegister() {
        return true;
    }

    /**
     * Determines if this loader is allowed to scan for data within mod JAR files.
     *
     * @return true to enable scanning within mod assets. Defaults to true.
     */
    default boolean isModLoader() {
        return true;
    }

    /**
     * Determines if this loader supports scanning for custom data overrides
     * within the specific world save directory.
     * <p>
     * Enable this to support server-side or world-specific data packs.
     *
     * @return true to enable scanning within the world save folder. Defaults to false.
     */
    default boolean isWorldLoader() {
        return false;
    }
}
