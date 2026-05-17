package ruiseki.okcore.data.loader.baubles;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.data.loader.DataLoader;
import ruiseki.okcore.data.loader.IDataLoader;
import ruiseki.okcore.lib.LibMods;

@DataLoader
public class BaublesExpandedLoader implements IDataLoader {

    @Override
    public String getTargetFolder() {
        return "baubles";
    }

    @Override
    public void process(ResourceLocation id, String namespace, String folder, String[] subPaths, String fileName,
        InputStream inputStream) {
        if (subPaths.length > 0 && subPaths[0].equals("slots")) {
            BaubleSlotReader reader = new BaubleSlotReader(id, fileName);
            try {
                BaubleSlotMaterial material = reader.read(inputStream);
                if (material == null) return;
                BaubleSlotHandler.queueMaterial(material);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read and batch bauble slot config: " + fileName, e);
            }
        }
    }

    @Override
    public boolean shouldRegister() {
        return LibMods.BaublesExpanded.isLoaded();
    }
}
