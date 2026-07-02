package ruiseki.okcore.data.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@LoadCondition
public class AndCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:and";
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
            if (cond == null || !cond.test(subJson)) return false;
        }

        return true;
    }
}
