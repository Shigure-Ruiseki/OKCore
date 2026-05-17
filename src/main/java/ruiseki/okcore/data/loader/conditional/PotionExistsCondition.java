package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.potion.Potion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

@LoadCondition("okcore:potion_exists")
public class PotionExistsCondition extends AbstractJsonMaterial {

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

        for (String val : this.values) {
            try {
                int id = Integer.parseInt(val);
                if (id < 0 || id >= Potion.potionTypes.length || Potion.potionTypes[id] == null) {
                    return false;
                }
            } catch (NumberFormatException e) {
                boolean found = false;
                for (Potion potion : Potion.potionTypes) {
                    if (potion != null && potion.getName() != null
                        && potion.getName()
                            .equalsIgnoreCase(val)) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }
        return true;
    }
}
