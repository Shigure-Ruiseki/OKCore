package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

@LoadCondition("okcore:tile_entity_exists")
public class TileEntityExistsCondition extends AbstractJsonMaterial {

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
        if (this.values.isEmpty()) {
            return true;
        }

        for (String tileName : this.values) {
            try {
                if (!TileEntity.nameToClassMap.containsKey(tileName)) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
