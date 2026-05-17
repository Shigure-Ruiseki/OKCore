package ruiseki.okcore.data.loader.baubles;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.data.loader.DataReader;

public class BaubleSlotReader extends DataReader<BaubleSlotMaterial> {

    public BaubleSlotReader(String fileName) {
        super(fileName);
    }

    @Override
    protected BaubleSlotMaterial readData(JsonElement root, String fileName) {
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
