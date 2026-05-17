package ruiseki.okcore.data.loader.conditional;

import com.google.gson.JsonObject;

import ruiseki.okcore.json.AbstractJsonMaterial;

@LoadCondition("okcore:not")
public class NotLoadCondition extends AbstractJsonMaterial {

    private AbstractJsonMaterial condition;

    @Override
    public void read(JsonObject json) {
        if (json.has("value") && json.get("value")
            .isJsonObject()) {
            JsonObject subJson = json.getAsJsonObject("value");

            AbstractJsonMaterial cond = LoadConditionHandler.createConditionInstance(subJson);
            if (cond != null) {
                cond.read(subJson);
                this.condition = cond;
            }
        }
    }

    @Override
    public void write(JsonObject json) {}

    @Override
    public boolean validate() {
        if (this.condition == null) {
            return false;
        }
        return !this.condition.validate();
    }
}
