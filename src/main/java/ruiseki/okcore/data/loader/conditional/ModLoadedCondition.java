package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.Loader;
import ruiseki.okcore.json.AbstractJsonMaterial;

@LoadCondition("okcore:mod_loaded")
public class ModLoadedCondition extends AbstractJsonMaterial {

    private final List<String> values = new ArrayList<>();

    @Override
    public void read(JsonObject json) {
        if (json.has("values") && json.get("values")
            .isJsonArray()) {
            JsonArray valuesArray = json.getAsJsonArray("values");

            for (JsonElement element : valuesArray) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                    .isString()) {
                    String modId = element.getAsString()
                        .trim();
                    if (!modId.isEmpty()) {
                        this.values.add(modId);
                    }
                }
            }
        }
        captureUnknownProperties(json, "values");
    }

    @Override
    public void write(JsonObject json) {

    }

    @Override
    public boolean validate() {
        if (this.values.isEmpty()) {
            return true;
        }

        for (String modId : this.values) {
            if (!Loader.isModLoaded(modId)) {
                return false;
            }
        }

        return true;
    }
}
