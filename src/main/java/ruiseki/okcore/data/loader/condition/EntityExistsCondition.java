package ruiseki.okcore.data.loader.condition;

import net.minecraft.entity.EntityList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@LoadCondition
public class EntityExistsCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:entity_exists";
    }

    @Override
    public boolean test(JsonObject json) {
        if (!json.has("values") || !json.get("values")
            .isJsonArray()) return true;

        JsonArray valuesArray = json.getAsJsonArray("values");
        for (JsonElement element : valuesArray) {
            if (element.isJsonPrimitive()) {
                String entityName = element.getAsString()
                    .trim();
                if (!EntityList.stringToClassMapping.containsKey(entityName)) {
                    return false;
                }
            }
        }
        return true;
    }
}
