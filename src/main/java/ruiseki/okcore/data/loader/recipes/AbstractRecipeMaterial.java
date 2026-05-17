package ruiseki.okcore.data.loader.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.item.ItemMaterial;

public abstract class AbstractRecipeMaterial<T extends IRecipe> implements IRecipeSerializer<T> {

    protected ItemMaterial result;

    @Override
    public void fromJson(ResourceLocation id, JsonObject json) {
        if (json.has("result")) {
            JsonElement resultElement = json.get("result");
            if (resultElement.isJsonObject()) {
                JsonObject resultObj = resultElement.getAsJsonObject();
                this.result = new ItemMaterial();
                this.result.read(resultObj);
            }
        }
    }

    @Override
    public boolean validate() {
        if (this.result == null) {
            OKCore.okLog("Recipe result cannot be empty!");
            return false;
        }
        return this.result.validate();
    }

    public ItemMaterial getResult() {
        return result;
    }
}
