package ruiseki.okcore.data.loader;

import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

public interface IDataLoader {

    String getTargetFolder();

    void process(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream);

    default boolean shouldRegister() {
        return true;
    }
}
