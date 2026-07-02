package ruiseki.okcore.data.condition;

import net.minecraft.potion.Potion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@LoadCondition
public class PotionExistsCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:potion_exists";
    }

    @Override
    public boolean test(JsonObject json) {
        if (!json.has("values") || !json.get("values")
            .isJsonArray()) return true;

        JsonArray valuesArray = json.getAsJsonArray("values");
        for (JsonElement element : valuesArray) {
            if (element.isJsonPrimitive()) {
                String val = element.getAsString()
                    .trim();
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
        }
        return true;
    }
}
