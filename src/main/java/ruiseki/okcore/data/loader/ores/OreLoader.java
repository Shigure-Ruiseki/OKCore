package ruiseki.okcore.data.loader.ores;

import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;

@DataLoader
public class OreLoader implements IDataLoader {

    @Override
    public String getTargetFolder() {
        return "oredicts";
    }

    @Override
    public void process(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream) {

    }
}
