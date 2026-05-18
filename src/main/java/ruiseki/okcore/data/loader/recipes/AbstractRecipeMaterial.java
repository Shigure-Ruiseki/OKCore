package ruiseki.okcore.data.loader.recipes;

import static ruiseki.okcore.data.loader.conditional.LoadConditionHandler.CONDITION_KEY;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataHandler;
import ruiseki.okcore.json.item.ItemMaterial;

public abstract class AbstractRecipeMaterial<T extends IRecipe> implements IRecipeSerializer<T> {

    protected ResourceLocation id;
    protected ItemMaterial result;
    protected JsonArray conditionsArray;

    @Override
    public void fromJson(ResourceLocation id, JsonObject json) {
        this.id = id;
        if (json.has(CONDITION_KEY) && json.get(CONDITION_KEY)
            .isJsonArray()) {
            this.conditionsArray = json.getAsJsonArray(CONDITION_KEY);
        } else {
            this.conditionsArray = new JsonArray();
        }
    }

    @Override
    public boolean validate() {
        if (!DataHandler.checkConditional(id.toString(), conditionsArray)) {
            return false;
        }
        if (this.result == null) {
            OKCore.okLog("Recipe result cannot be empty!");
            return false;
        }
        return this.result.validate();
    }

    public ItemMaterial getResult() {
        return result;
    }

    public ResourceLocation getId() {
        return id;
    }
}
