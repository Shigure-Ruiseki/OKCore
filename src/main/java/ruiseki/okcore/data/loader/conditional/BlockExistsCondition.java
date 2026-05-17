package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;
import ruiseki.okcore.json.block.BlockMaterial;

@LoadCondition("okcore:block_exists")
public class BlockExistsCondition extends AbstractJsonMaterial {

    private final List<BlockMaterial> values = new ArrayList<>();

    @Override
    public void read(JsonObject json) {
        if (json.has("values") && json.get("values")
            .isJsonArray()) {
            JsonArray valuesArray = json.getAsJsonArray("values");
            for (JsonElement element : valuesArray) {
                if (element.isJsonObject()) {
                    BlockMaterial mat = new BlockMaterial();
                    mat.read(element.getAsJsonObject());

                    if (mat.validate()) {
                        this.values.add(mat);
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

        for (BlockMaterial mat : this.values) {
            if (!mat.validate()) return false;
        }

        return true;
    }
}
