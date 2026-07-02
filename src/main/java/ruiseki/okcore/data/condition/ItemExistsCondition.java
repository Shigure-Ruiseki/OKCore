package ruiseki.okcore.data.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.item.ItemMaterial;

@LoadCondition
public class ItemExistsCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:item_exists";
    }

    @Override
    public boolean test(JsonObject json) {
        if (!json.has("values") || !json.get("values")
            .isJsonArray()) return true;

        JsonArray valuesArray = json.getAsJsonArray("values");
        for (JsonElement element : valuesArray) {
            if (element.isJsonObject()) {
                ItemMaterial mat = new ItemMaterial();
                mat.read(element.getAsJsonObject());

                if (!mat.validate()) {
                    return false;
                }
            }
        }
        return true;
    }
}
