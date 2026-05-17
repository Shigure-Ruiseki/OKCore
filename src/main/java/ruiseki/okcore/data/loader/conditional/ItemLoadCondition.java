package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;
import ruiseki.okcore.json.item.ItemMaterial;

@LoadCondition("okcore:item_exists")
public class ItemLoadCondition extends AbstractJsonMaterial {

    private final List<ItemMaterial> values = new ArrayList<>();

    @Override
    public void read(JsonObject json) {
        if (json.has("values") && json.get("values")
            .isJsonArray()) {
            JsonArray valuesArray = json.getAsJsonArray("values");
            for (JsonElement element : valuesArray) {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                    .isString()) {
                    String valueStr = element.getAsString();
                    ItemMaterial mat = new ItemMaterial();

                    if (valueStr.startsWith("ore:")) {
                        mat.ore = valueStr.substring(4);
                    } else if (valueStr.contains(":")) {
                        mat.item = valueStr;
                    } else {
                        mat.ore = valueStr;
                    }

                    if (mat.validate()) {
                        this.values.add(mat);
                    }
                } else if (element.isJsonObject()) {
                    ItemMaterial mat = new ItemMaterial();
                    mat.read(element.getAsJsonObject());

                    if (mat.validate()) {
                        this.values.add(mat);
                    }
                }
            }
        }
    }

    @Override
    public void write(JsonObject json) {

    }

    @Override
    public boolean validate() {
        if (this.values.isEmpty()) {
            return true;
        }

        for (ItemMaterial mat : this.values) {
            ItemStack stack = mat.toStack();
            if (stack == null || stack.getItem() == null) {
                return false;
            }
        }

        return true;
    }
}
