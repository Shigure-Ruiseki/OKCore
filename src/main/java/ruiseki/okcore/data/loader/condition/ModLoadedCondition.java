package ruiseki.okcore.data.loader.condition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.Loader;

@LoadCondition
public class ModLoadedCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:mod_loaded";
    }

    @Override
    public boolean test(JsonObject json) {
        if (!json.has("values") || !json.get("values")
            .isJsonArray()) return true;

        JsonArray valuesArray = json.getAsJsonArray("values");
        for (JsonElement element : valuesArray) {
            if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                .isString()) {
                String modId = element.getAsString()
                    .trim();
                if (!modId.isEmpty() && !Loader.isModLoaded(modId)) {
                    return false;
                }
            }
        }
        return true;
    }
}
