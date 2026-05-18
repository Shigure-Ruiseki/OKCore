package ruiseki.okcore.data.loader.recipes;

import static ruiseki.okcore.data.loader.conditional.LoadConditionHandler.CONDITION_KEY;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

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
            OKCore.okLog(Level.ERROR, "Recipe [{}] result cannot be empty!", id);
            return false;
        }
        if (this.result == null) {
            OKCore.okLog(Level.ERROR, "Recipe [{}] result cannot be empty!", id);
            return false;
        }

        if (!this.result.validate()) {
            OKCore.okLog(
                Level.ERROR,
                "Recipe [{}] validation failed: Output 'result' material is invalid in current game registry!",
                id);
            return false;
        }
        return true;
    }

    public ItemMaterial getResult() {
        return result;
    }

    public ResourceLocation getId() {
        return id;
    }
}
