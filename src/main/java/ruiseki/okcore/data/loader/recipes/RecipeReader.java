package ruiseki.okcore.data.loader.recipes;

import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.data.loader.DataReader;

public class RecipeReader extends DataReader<AbstractRecipeMaterial> {

    public RecipeReader(File path) {
        super(path);
    }

    @Override
    protected AbstractRecipeMaterial readData(JsonElement root, File file) {
        if (root.isJsonObject()) {
            JsonObject json = root.getAsJsonObject();
            String type = json.has("type") ? json.get("type")
                .getAsString() : "";
            AbstractRecipeMaterial material = RecipeHandler.createMaterial(type);
            if (material != null) {
                material.setSourceFile(file);
                material.read(json);
                return material;
            }
        }
        return null;
    }
}
