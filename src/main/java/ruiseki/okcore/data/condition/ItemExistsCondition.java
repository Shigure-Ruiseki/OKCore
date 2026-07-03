package ruiseki.okcore.data.condition;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.Helpers;

public class ItemExistsCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation("okcore", "item_exists");
    private final ResourceLocation item;

    public ItemExistsCondition(String location) {
        this(new ResourceLocation(location));
    }

    public ItemExistsCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public ItemExistsCondition(ResourceLocation item) {
        this.item = item;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        return GameData.getItemRegistry()
            .containsKey(item.toString());
    }

    @Override
    public String toString() {
        return "item_exists(\"" + item + "\")";
    }

    public static class Serializer implements IConditionSerializer<ItemExistsCondition> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ItemExistsCondition value) {
            json.addProperty("item", value.item.toString());
        }

        @Override
        public ItemExistsCondition read(JsonObject json) {
            return new ItemExistsCondition(Helpers.parseLocation(GsonHelpers.getAsString(json, "item")));
        }

        @Override
        public ResourceLocation getID() {
            return ItemExistsCondition.NAME;
        }
    }
}
