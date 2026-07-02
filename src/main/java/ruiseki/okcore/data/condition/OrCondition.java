package ruiseki.okcore.data.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@LoadCondition
public class OrCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:or";
    }

    @Override
    public boolean test(JsonObject json) {
        if (!json.has("values") || !json.get("values")
            .isJsonArray()) return false;

        JsonArray valuesArray = json.getAsJsonArray("values");
        if (valuesArray.size() == 0) return false;

        for (JsonElement element : valuesArray) {
            if (!element.isJsonObject()) return false;

            JsonObject subJson = element.getAsJsonObject();
            ILoadCondition cond = LoadRegistry.createConditionInstance(subJson);
            if (cond == null) return false;

            if (cond.test(subJson)) return true;
        }

        return false;
    }
}
