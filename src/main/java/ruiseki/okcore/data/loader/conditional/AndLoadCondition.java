package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

@LoadCondition("okcore:and")
public class AndLoadCondition extends AbstractJsonMaterial {

    private final List<AbstractJsonMaterial> conditions = new ArrayList<>();

    @Override
    public void read(JsonObject json) {
        if (json.has("values") && json.get("values")
            .isJsonArray()) {
            JsonArray valuesArray = json.getAsJsonArray("values");
            for (JsonElement element : valuesArray) {
                if (element.isJsonObject()) {
                    JsonObject subJson = element.getAsJsonObject();
                    AbstractJsonMaterial cond = LoadConditionHandler.createConditionInstance(subJson);
                    if (cond != null) {
                        cond.read(subJson);
                        this.conditions.add(cond);
                    }
                }
            }
        }
    }

    @Override
    public void write(JsonObject json) {}

    @Override
    public boolean validate() {
        if (this.conditions.isEmpty()) {
            return true;
        }

        for (AbstractJsonMaterial cond : this.conditions) {
            if (!cond.validate()) {
                return false;
            }
        }
        return true;
    }
}
