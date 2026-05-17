package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

@LoadCondition("okcore:entity_exists")
public class EntityExistsCondition extends AbstractJsonMaterial {

    private final List<String> values = new ArrayList<>();

    @Override
    public void read(JsonObject json) {
        if (json.has("values") && json.get("values")
            .isJsonArray()) {
            JsonArray valuesArray = json.getAsJsonArray("values");
            for (JsonElement element : valuesArray) {
                if (element.isJsonPrimitive()) {
                    this.values.add(
                        element.getAsString()
                            .trim());
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
        if (this.values.isEmpty()) return true;

        for (String entityName : this.values) {
            if (!EntityList.stringToClassMapping.containsKey(entityName)) {
                return false;
            }
        }
        return true;
    }
}
