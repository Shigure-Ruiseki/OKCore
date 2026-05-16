package ruiseki.okcore.data.loader.recipes;

import java.util.List;

import net.minecraft.item.crafting.IRecipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;
import ruiseki.okcore.json.item.ItemMaterial;

public abstract class AbstractRecipeMaterial extends AbstractJsonMaterial {

    protected String type;
    protected ItemMaterial result;

    @Override
    public void read(JsonObject json) {
        this.type = getString(json, "type", "");
        if (json.has("result")) {
            JsonElement resultElement = json.get("result");
            if (resultElement.isJsonObject()) {
                JsonObject resultObj = resultElement.getAsJsonObject();
                this.result = new ItemMaterial();
                this.result.read(resultObj);
            }
        }
        readSpecific(json);
    }

    protected abstract void readSpecific(JsonObject json);

    protected abstract List<IRecipe> getRecipes();

    @Override
    public boolean validate() {
        if (type.isEmpty()) {
            logValidationError("Recipe type cannot be empty!");
            return false;
        }
        if (this.result == null) {
            logValidationError("Recipe result is missing!");
            return false;
        }
        if (!this.result.validate()) {
            return false;
        }

        return validateSpecific();
    }

    protected boolean validateSpecific() {
        return true;
    }

    public String getType() {
        return type;
    }

    public ItemMaterial getResult() {
        return result;
    }

    @Override
    public void write(JsonObject json) {
        // Read only
    }
}
