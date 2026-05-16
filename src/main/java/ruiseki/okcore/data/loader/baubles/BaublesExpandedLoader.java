package ruiseki.okcore.data.loader.baubles;

import java.io.File;
import java.io.IOException;

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
    public boolean shouldRegister() {
        return LibMods.BaublesExpanded.isLoaded();
    }

    @Override
    public void process(String namespace, String folder, String[] subPaths, String fileName, File json) {
        if (subPaths.length > 0 && subPaths[0].equals("slots")) {
            BaubleSlotReader reader = new BaubleSlotReader(json);
            try {
                BaubleSlotMaterial material = reader.read();
                if (material == null) return;
                BaubleSlotHandler.queueMaterial(material);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read and batch bauble slot config: " + json.getName(), e);
            }
        }

    }
}
