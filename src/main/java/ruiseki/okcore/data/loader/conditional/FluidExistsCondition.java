package ruiseki.okcore.data.loader.conditional;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;
import ruiseki.okcore.json.fluid.FluidMaterial;

@LoadCondition("okcore:fluid_exists")
public class FluidExistsCondition extends AbstractJsonMaterial {

    private final List<FluidMaterial> values = new ArrayList<>();

    @Override
    public void read(JsonObject json) {
        if (json.has("values") && json.get("values")
            .isJsonArray()) {
            JsonArray valuesArray = json.getAsJsonArray("values");
            for (JsonElement element : valuesArray) {
                if (element.isJsonObject()) {
                    FluidMaterial mat = new FluidMaterial();
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

        for (FluidMaterial mat : this.values) {
            if (!mat.validate()) return false;
        }

        return true;
    }
}
