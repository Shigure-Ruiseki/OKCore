package ruiseki.okcore.data.condition;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.Loader;
import ruiseki.okcore.helper.GsonHelpers;

public class ModLoadedCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation("okcore", "mod_loaded");
    private final String modid;

    public ModLoadedCondition(String modid) {
        this.modid = modid;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return Loader.isModLoaded(modid);
    }

    @Override
    public String toString() {
        return "mod_loaded(\"" + modid + "\")";
    }

    public static class Serializer implements IConditionSerializer<ModLoadedCondition> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ModLoadedCondition value) {
            json.addProperty("modid", value.modid);
        }

        @Override
        public ModLoadedCondition read(JsonObject json) {
            return new ModLoadedCondition(GsonHelpers.getAsString(json, "modid"));
        }

        @Override
        public ResourceLocation getID() {
            return ModLoadedCondition.NAME;
        }
    }
}
