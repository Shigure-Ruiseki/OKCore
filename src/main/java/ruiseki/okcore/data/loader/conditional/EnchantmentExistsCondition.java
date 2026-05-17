package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.enchantment.Enchantment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

@LoadCondition("okcore:enchantment_exists")
public class EnchantmentExistsCondition extends AbstractJsonMaterial {

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
                if (id < 0 || id >= Enchantment.enchantmentsList.length || Enchantment.enchantmentsList[id] == null) {
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
        return true;
    }
}
