package ruiseki.okcore.data.loader.baubles;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.data.loader.DataReader;

public class BaubleSlotReader extends DataReader<BaubleSlotMaterial> {

    public BaubleSlotReader(ResourceLocation id, String fileName) {
        super(id, fileName);
    }

    @Override
    protected BaubleSlotMaterial readData(ResourceLocation id, JsonElement root, String fileName) {
        if (root.isJsonObject()) {
            JsonObject json = root.getAsJsonObject();
            String slotType = fileName.endsWith(".json") ? fileName.substring(0, fileName.length() - 5) : fileName;

            BaubleSlotMaterial material = new BaubleSlotMaterial(slotType);
            material.read(json);
            if (!material.validate()) return null;

            return material;
        }
        return null;
    }
}
