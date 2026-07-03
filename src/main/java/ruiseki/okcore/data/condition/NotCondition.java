package ruiseki.okcore.data.condition;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import ruiseki.okcore.helper.GsonHelpers;

public class NotCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation("okcore", "not");
    private final ICondition child;

    public NotCondition(ICondition child) {
        this.child = child;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return !child.test(context);
    }

    @Override
    public String toString() {
        return "!" + child;
    }

    public static class Serializer implements IConditionSerializer<NotCondition> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, NotCondition value) {
            json.add("value", ConditionRegistry.serialize(value.child));
        }

        @Override
        public NotCondition read(JsonObject json) {
            return new NotCondition(ConditionRegistry.getCondition(GsonHelpers.getAsJsonObject(json, "value")));
        }

        @Override
        public ResourceLocation getID() {
            return NotCondition.NAME;
        }
    }
}
