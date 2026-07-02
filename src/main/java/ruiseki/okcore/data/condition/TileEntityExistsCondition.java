package ruiseki.okcore.data.condition;

import net.minecraft.tileentity.TileEntity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@LoadCondition
public class TileEntityExistsCondition implements ILoadCondition {

    @Override
    public String getID() {
        return "okcore:tile_entity_exists";
    }

    @Override
    public boolean test(JsonObject json) {
        if (!json.has("values") || !json.get("values")
            .isJsonArray()) return true;

        JsonArray valuesArray = json.getAsJsonArray("values");
        for (JsonElement element : valuesArray) {
            if (element.isJsonPrimitive()) {
                String tileName = element.getAsString()
                    .trim();
                if (!TileEntity.nameToClassMap.containsKey(tileName)) {
                    return false;
                }
            }
        }
        return true;
    }
}
