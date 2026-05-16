package ruiseki.okcore.data.loader.recipes;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonReader;

public class RecipeReader extends AbstractJsonReader<AbstractRecipeMaterial> {

    public RecipeReader(File path) {
        super(path);
    }

    @Override
    public AbstractRecipeMaterial read() throws IOException {
        return readFile(path);
    }

    @Override
    protected AbstractRecipeMaterial readFile(JsonElement root, File file) {
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
