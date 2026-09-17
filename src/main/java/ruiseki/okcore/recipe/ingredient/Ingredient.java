package ruiseki.okcore.recipe.ingredient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameData;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.RecipeItemHelpers;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;

public class Ingredient implements Predicate<ItemStack> {

    private static final AtomicInteger INVALIDATION_COUNTER = new AtomicInteger();

    public static void invalidateAll() {
        INVALIDATION_COUNTER.incrementAndGet();
    }

    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Ingredient.IItemList[] values;
    @Nullable
    private ItemStack[] itemStacks;
    @Nullable
    private IntList stackingIds;
    private int invalidationCounter = -1;

    protected Ingredient(Stream<? extends IItemList> stream) {
        this.values = stream.toArray(IItemList[]::new);
    }

    public ItemStack[] getItems() {
        if (this.itemStacks == null || this.itemStacks.length == 0 || checkInvalidation()) {
            this.markValid();
            this.itemStacks = Arrays.stream(this.values)
                .map(IItemList::getItems)
                .<ItemStack>mapMulti((items, consumer) -> {
                    for (ItemStack stack : items) {
                        if (!ItemHelpers.isEmpty(stack)) {
                            consumer.accept(stack);
                        }
                    }
                })
                .distinct()
                .toArray(ItemStack[]::new);
        }
        return this.itemStacks;
    }

    public boolean test(@Nullable ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) {
            return false;
        } else if (this.isEmpty()) {
            return ItemHelpers.isEmpty(stack);
        } else {
            for (ItemStack itemstack : this.getItems()) {
                if (ItemHelpers.areItemsEqual(stack, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    public IntList getStackingIds() {
        if (this.stackingIds == null || checkInvalidation()) {
            this.markValid();
            ItemStack[] aitemstack = this.getItems();
            this.stackingIds = new IntArrayList(aitemstack.length);

            for (ItemStack itemstack : aitemstack) {
                this.stackingIds.add(RecipeItemHelpers.getStackingIndex(itemstack));
            }

            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.stackingIds;
    }

    public final void toNetwork(ExtendedBuffer buffer) {
        if (!this.isVanilla()) {
            RecipeRegistry.toNetwork(buffer, this);
            return;
        }

        buffer.writeCollection(Arrays.asList(this.getItems()), (writer, stack) -> {
            try {
                writer.writeItemStackToBuffer(stack);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();

            for (Ingredient.IItemList ingredient$value : this.values) {
                jsonarray.add(ingredient$value.serialize());
            }

            return jsonarray;
        }
    }

    public boolean isEmpty() {
        return this.values.length == 0;
    }

    public final boolean checkInvalidation() {
        int currentInvalidationCounter = INVALIDATION_COUNTER.get();
        if (this.invalidationCounter != currentInvalidationCounter) {
            invalidate();
            return true;
        }
        return false;
    }

    protected final void markValid() {
        this.invalidationCounter = INVALIDATION_COUNTER.get();
    }

    protected void invalidate() {
        this.itemStacks = null;
        this.stackingIds = null;
    }

    public boolean isSimple() {
        return true;
    }

    private final boolean isVanilla = this.getClass() == Ingredient.class;

    public final boolean isVanilla() {
        return isVanilla;
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        if (!isVanilla()) throw new IllegalStateException(
            "Modders must implement Ingredient.getSerializer in their custom Ingredients: " + this);
        return VanillaIngredientSerializer.INSTANCE;
    }

    public static Ingredient fromValues(Stream<? extends Ingredient.IItemList> list) {
        Ingredient ingredient = new Ingredient(list);
        return ingredient.isEmpty() ? EMPTY : ingredient;
    }

    public static Ingredient of() {
        return EMPTY;
    }

    public static Ingredient of(Object input) {
        if (input == null) {
            return EMPTY;
        } else if (input instanceof Ingredient) {
            return (Ingredient) input;
        } else if (input instanceof ItemStack) {
            return of(new ItemStack[] { (ItemStack) input });
        } else if (input instanceof Item) {
            return of(new Item[] { (Item) input });
        } else if (input instanceof String) {
            return fromValues(Stream.of(new OreList((String) input)));
        } else if (input instanceof List<?>list) {
            if (list.isEmpty()) {
                return EMPTY;
            } else {
                Stream<ItemStack> stream = list.stream()
                    .filter(ItemStack.class::isInstance)
                    .map(ItemStack.class::cast);
                return of(stream);
            }
        } else if (input instanceof ItemStack[]) {
            return of((ItemStack[]) input);
        } else {
            throw new IllegalArgumentException(
                "Cannot convert object of type " + input.getClass()
                    .getName() + " to Ingredient");
        }
    }

    public static Ingredient of(Item... items) {
        return of(
            Arrays.stream(items)
                .map(ItemStack::new));
    }

    public static Ingredient of(ItemStack... stacks) {
        return of(Arrays.stream(stacks));
    }

    public static Ingredient of(Stream<ItemStack> stacks) {
        return fromValues(
            stacks.filter(Objects::nonNull)
                .map(SingleItemList::new));
    }

    public static Ingredient of(TagKey<Item> tag) {
        return fromValues(Stream.of(new Ingredient.TagList(tag)));
    }

    public static Ingredient fromNetwork(ExtendedBuffer buffer) {
        int i = buffer.readVarIntFromBuffer();
        if (i == -1) {
            return RecipeRegistry.getIngredient(buffer.readResourceLocation(), buffer);
        }
        return fromValues(Stream.generate(() -> {
            try {
                return new SingleItemList(buffer.readItemStackFromBuffer());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
            .limit(i));
    }

    public static Ingredient fromJson(@Nullable JsonElement jsonElement) {
        return fromJson(jsonElement, true);
    }

    public static Ingredient fromJson(JsonElement jsonElement, boolean allowEmpty) {
        if (jsonElement != null && !jsonElement.isJsonNull()) {
            Ingredient ret = RecipeRegistry.getIngredient(jsonElement, allowEmpty);
            if (ret != null) return ret;
            if (jsonElement.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(jsonElement.getAsJsonObject())));
            } else if (jsonElement.isJsonArray()) {
                JsonArray jsonarray = jsonElement.getAsJsonArray();
                if (jsonarray.size() == 0 && !allowEmpty) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return fromValues(
                        StreamSupport.stream(jsonarray.spliterator(), false)
                            .map(
                                (p_209355_0_) -> {
                                    return valueFromJson(GsonHelpers.convertToJsonObject(p_209355_0_, "item"));
                                }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static Ingredient.IItemList valueFromJson(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else if (json.has("item")) {
            ItemStack stack = ShapedRecipe.itemFromJson(json);
            return new Ingredient.SingleItemList(stack);
        } else if (json.has("tag")) {
            String tagName = GsonHelpers.getAsString(json, "tag");
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, new ResourceLocation(tagName));
            return new Ingredient.TagList(tagKey);
        } else if (json.has("ore")) {
            String ore = GsonHelpers.getAsString(json, "ore");
            return new Ingredient.OreList(ore);
        } else {
            throw new JsonParseException("An ingredient entry needs either a tag or an item");
        }
    }

    public static Ingredient merge(Collection<Ingredient> parts) {
        return fromValues(
            parts.stream()
                .flatMap(i -> Arrays.stream(i.values)));
    }

    public interface IItemList {

        Collection<ItemStack> getItems();

        JsonObject serialize();
    }

    public static class SingleItemList implements Ingredient.IItemList {

        private final ItemStack stack;

        public SingleItemList(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public Collection<ItemStack> getItems() {
            return Collections.singletonList(this.stack);
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(
                "item",
                GameData.getItemRegistry()
                    .getNameForObject(this.stack.getItem()));
            return jsonobject;
        }
    }

    public static class TagList implements Ingredient.IItemList {

        private final TagKey<Item> tag;

        public TagList(TagKey<Item> tag) {
            this.tag = tag;
        }

        @Override
        public Collection<ItemStack> getItems() {
            List<ItemStack> list = TagHelpers.toItemStacks(this.tag);
            if (list.isEmpty()) return Collections.emptyList();
            return list;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(
                "tag",
                this.tag.location()
                    .toString());
            return jsonobject;
        }
    }

    public static class OreList implements IItemList {

        private final String ore;

        public OreList(String ore) {
            this.ore = ore;
        }

        @Override
        public Collection<ItemStack> getItems() {
            if (!OreDictionary.doesOreNameExist(this.ore)) return Collections.emptyList();
            List<ItemStack> list = OreDictionary.getOres(this.ore);
            if (list == null || list.isEmpty()) return Collections.emptyList();
            return list;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("ore", this.ore);
            return jsonobject;
        }
    }
}
