package ruiseki.okcore.data.loader.baubles;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonReader;

public class BaubleSlotReader extends AbstractJsonReader<BaubleSlotMaterial> {

    public BaubleSlotReader(File path) {
        super(path);
    }

    @Override
    public BaubleSlotMaterial read() throws IOException {
        return readFile(path);
    }

    @Override
    protected BaubleSlotMaterial readFile(JsonElement root, File file) {
        if (root.isJsonObject()) {
            JsonObject json = root.getAsJsonObject();
            String fileName = file.getName();
            String slotType = fileName.endsWith(".json") ? fileName.substring(0, fileName.length() - 5) : fileName;

            BaubleSlotMaterial material = new BaubleSlotMaterial(slotType);
            material.setSourceFile(file);
            material.read(json);
            if (!material.validate()) return null;

            return material;
        }
        return null;
    }
}
