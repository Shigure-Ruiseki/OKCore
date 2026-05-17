package ruiseki.okcore.data.loader;

import java.io.InputStream;

public interface IDataLoader {

    String getTargetFolder();

    void process(String namespace, String folder, String[] subPaths, String fileName, InputStream inputStream);

    default boolean shouldRegister() {
        return true;
    }
}
