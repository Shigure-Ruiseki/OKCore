package ruiseki.okcore.data.loader.condition;

import com.google.gson.JsonObject;

@LoadCondition
public class NotCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:not";
    }

    @Override
    public boolean test(JsonObject json) {
        if (!json.has("value") || !json.get("value")
            .isJsonObject()) return false;

        JsonObject subJson = json.getAsJsonObject("value");
        ILoadCondition cond = LoadRegistry.createConditionInstance(subJson);
        if (cond == null) return false;

        return !cond.test(subJson);
    }
}
