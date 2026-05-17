package ruiseki.okcore.data.loader.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.data.loader.DataReader;

public class RecipeReader extends DataReader<IRecipeSerializer<IRecipe>> {

    public RecipeReader(ResourceLocation id, String fileName) {
        super(id, fileName);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected IRecipeSerializer<IRecipe> readData(ResourceLocation id, JsonElement root, String fileName) {
        if (root.isJsonObject()) {
            JsonObject json = root.getAsJsonObject();
            String type = json.has("type") ? json.get("type")
                .getAsString() : "";
            IRecipeSerializer<IRecipe> material = (IRecipeSerializer<IRecipe>) RecipeHandler.getSerializer(type);
            if (material != null) {
                material.fromJson(id, json);
                return material;
            }
        }
        return null;
    }
}
