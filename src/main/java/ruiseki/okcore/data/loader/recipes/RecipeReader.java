package ruiseki.okcore.data.loader.recipes;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.data.loader.DataReader;

public class RecipeReader extends DataReader<RecipeHolder> {

    public RecipeReader(ResourceLocation id, String fileName) {
        super(id, fileName);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected RecipeHolder readData(ResourceLocation id, JsonElement root, String fileName) {
        if (root.isJsonObject()) {
            JsonObject json = root.getAsJsonObject();
            String type = json.has("type") ? json.get("type")
                .getAsString() : "";
            return new RecipeHolder(id, type, json, fileName);
        }
        return null;
    }
}
