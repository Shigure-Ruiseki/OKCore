package ruiseki.okcore.recipe.ingredient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
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
import ruiseki.okcore.helper.TagHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.RecipeItemHelpers;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.tag.Registries;
import ruiseki.okcore.tag.TagKey;

public class Ingredient implements Predicate<ItemStack> {

    private static final Set<Ingredient> INSTANCES = Collections.newSetFromMap(new WeakHashMap<>());

    public static void invalidateAll() {
        INSTANCES.stream()
            .filter(Objects::nonNull)
            .forEach(Ingredient::invalidate);
    }

    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final Ingredient.IItemList[] values;
    private ItemStack[] itemStacks;
    private IntList stackingIds;
    private final boolean isSimple;

    protected Ingredient(Stream<? extends IItemList> p_i49381_1_) {
        this.values = p_i49381_1_.toArray(IItemList[]::new);
        this.isSimple = Arrays.stream(values)
            .noneMatch(
                list -> list.getItems()
                    .stream()
                    .anyMatch(
                        stack -> stack.getItem() != null && stack.getItem()
                            .isDamageable()));
        Ingredient.INSTANCES.add(this);
    }

    public ItemStack[] getItems() {
        this.dissolve();
        return this.itemStacks;
    }

    private void dissolve() {
        if (this.itemStacks == null) {
            this.itemStacks = Arrays.stream(this.values)
                .flatMap(
                    (itemList) -> {
                        return itemList.getItems()
                            .stream();
                    })
                .distinct()
                .toArray(ItemStack[]::new);
        }
    }

    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        } else {
            this.dissolve();
            if (this.itemStacks.length == 0) {
                return false;
            } else {
                for (ItemStack itemstack : this.itemStacks) {
                    if (itemstack.getItem() == stack.getItem()) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public IntList getStackingIds() {
        if (this.stackingIds == null) {
            this.dissolve();
            this.stackingIds = new IntArrayList(this.itemStacks.length);

            for (ItemStack itemstack : this.itemStacks) {
                this.stackingIds.add(RecipeItemHelpers.getStackingIndex(itemstack));
            }

            this.stackingIds.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.stackingIds;
    }

    public final void toNetwork(ExtendedBuffer buffer) {
        this.dissolve();
        if (!this.isVanilla()) {
            RecipeRegistry.toNetwork(buffer, this);
            return;
        }
        buffer.writeVarIntToBuffer(this.itemStacks.length);

        for (int i = 0; i < this.itemStacks.length; ++i) {
            try {
                buffer.writeItemStackToBuffer(this.itemStacks[i]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();

            for (Ingredient.IItemList itemList : this.values) {
                jsonarray.add(itemList.serialize());
            }

            return jsonarray;
        }
    }

    public boolean isEmpty() {
        return this.values.length == 0 && (this.itemStacks == null || this.itemStacks.length == 0)
            && (this.stackingIds == null || this.stackingIds.isEmpty());
    }

    protected void invalidate() {
        this.itemStacks = null;
        this.stackingIds = null;
    }

    public boolean isSimple() {
        return isSimple || this == EMPTY;
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
        return ingredient.values.length == 0 ? EMPTY : ingredient;
    }

    public static Ingredient of(Object input) {
        if (input == null) return EMPTY;

        if (input instanceof Ingredient) {
            return (Ingredient) input;
        }
        if (input instanceof ItemStack) {
            return of(input);
        }
        if (input instanceof Item) {
            return of(input);
        }
        if (input instanceof String string) {
            return fromValues(Stream.of(new OreList(string)));
        }
        if (input instanceof List<?>list) {
            if (list.isEmpty()) return EMPTY;
            Stream<ItemStack> stream = list.stream()
                .filter(ItemStack.class::isInstance)
                .map(ItemStack.class::cast);
            return of(stream);
        }
        if (input instanceof ItemStack[]) {
            return of((ItemStack[]) input);
        }

        throw new IllegalArgumentException(
            "Cannot convert object of type " + input.getClass()
                .getName() + " to Ingredient");
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
            .limit((long) i));
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

    // Merges several vanilla Ingredients together. As a quirk of how the json is structured, we can't tell if its a
    // single Ingredient type or multiple so we split per item and re-merge here.
    // Only public for internal use, so we can access a private field in here.
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

        public Collection<ItemStack> getItems() {
            return Collections.singleton(this.stack);
        }

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
            List<ItemStack> list = OreDictionary.getOres(this.ore);
            if (list.isEmpty()) return Collections.emptyList();
            return list;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", this.ore);
            return jsonobject;
        }
    }
}
