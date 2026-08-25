package ruiseki.okcore.data.condition;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.Helpers;

public class ConfigCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation("forge", "config");
    private static final Map<ResourceLocation, Boolean> CONFIG = new HashMap<>();

    private final ResourceLocation id;
    private final boolean condition;

    public ConfigCondition(ResourceLocation id, boolean condition) {
        this.id = id;
        this.condition = condition;
    }

    public static void registerConfig(ResourceLocation id, boolean value) {
        CONFIG.put(id, value);
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        Boolean actualValue = CONFIG.get(this.id);
        if (actualValue == null) {
            return false;
        }
        return actualValue == this.condition;
    }

    @Override
    public String toString() {
        return "config(\"" + id + "\" == " + condition + ")";
    }

    public static class Serializer implements IConditionSerializer<ConfigCondition> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ConfigCondition value) {
            json.addProperty("id", value.id.toString());
            json.addProperty("value", value.condition);
        }

        @Override
        public ConfigCondition read(JsonObject json) {
            ResourceLocation configId = Helpers.parseLocation(GsonHelpers.getAsString(json, "id"));
            boolean expectedValue = true;
            if (json.has("value")) {
                expectedValue = json.get("value")
                    .getAsBoolean();
            }

            return new ConfigCondition(configId, expectedValue);
        }

        @Override
        public ResourceLocation getID() {
            return ConfigCondition.NAME;
        }
    }
}
