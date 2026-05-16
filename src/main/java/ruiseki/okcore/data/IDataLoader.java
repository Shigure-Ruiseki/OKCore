package ruiseki.okcore.data;

import java.io.File;

public interface IDataLoader {

    String getTargetFolder();

    void process(String namespace, String folder, String[] subPaths, String fileName, File json);

    default boolean shouldRegister() {
        return true;
    }
}
