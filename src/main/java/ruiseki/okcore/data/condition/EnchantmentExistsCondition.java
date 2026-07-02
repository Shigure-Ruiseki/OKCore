package ruiseki.okcore.data.condition;

import net.minecraft.enchantment.Enchantment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@LoadCondition
public class EnchantmentExistsCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:enchantment_exists";
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
                    if (id < 0 || id >= Enchantment.enchantmentsList.length
                        || Enchantment.enchantmentsList[id] == null) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    boolean found = false;
                    for (Enchantment ench : Enchantment.enchantmentsList) {
                        if (ench != null && ench.getName() != null
                            && ench.getName()
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
