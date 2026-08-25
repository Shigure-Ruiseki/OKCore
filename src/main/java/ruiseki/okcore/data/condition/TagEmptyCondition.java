package ruiseki.okcore.data.condition;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;
import ruiseki.okcore.tag.TagManager;

public class TagEmptyCondition implements ICondition {

    private static final ResourceLocation NAME = new ResourceLocation("okcore", "tag_empty");
    private final TagKey<Item> tagKey;

    public TagEmptyCondition(TagKey<Item> tagKey) {
        this.tagKey = tagKey;
    }

    public TagEmptyCondition(ResourceLocation tagLocation) {
        this(TagKey.create(Registries.ITEM, tagLocation));
    }

    public TagEmptyCondition(String location) {
        this(new ResourceLocation(location));
    }

    public TagEmptyCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(IContext context) {
        if (tagKey == null) return true;
        return TagManager.getManager()
            .getEntries(tagKey)
            .isEmpty();
    }

    public TagKey<Item> getTagKey() {
        return tagKey;
    }

    public static class Serializer implements IConditionSerializer<TagEmptyCondition> {

        public static final TagEmptyCondition.Serializer INSTANCE = new TagEmptyCondition.Serializer();

        @Override
        public void write(JsonObject json, TagEmptyCondition value) {
            if (value.getTagKey() != null) {
                json.addProperty(
                    "tag",
                    value.getTagKey()
                        .location()
                        .toString());
            }
        }

        @Override
        public TagEmptyCondition read(JsonObject json) {
            ResourceLocation tagLocation = Helpers.parseLocation(GsonHelpers.getAsString(json, "tag"));
            return new TagEmptyCondition(tagLocation);
        }

        @Override
        public ResourceLocation getID() {
            return TagEmptyCondition.NAME;
        }
    }
}
