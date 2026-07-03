package ruiseki.okcore.data.condition;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

public class TrueCondition implements ICondition {

    public static final TrueCondition INSTANCE = new TrueCondition();
    private static final ResourceLocation NAME = new ResourceLocation("okcore", "true");

    private TrueCondition() {}

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return true;
    }

    @Override
    public String toString() {
        return "true";
    }

    public static class Serializer implements IConditionSerializer<TrueCondition> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, TrueCondition value) {}

        @Override
        public TrueCondition read(JsonObject json) {
            return TrueCondition.INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return TrueCondition.NAME;
        }
    }
}
