
package ruiseki.okcore.recipe.ingredient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.RecipeRegistry;

public class ExcludedIngredient extends Ingredient {

    private final ItemStack base;
    private final ItemStack[] excluded;

    public ExcludedIngredient(ItemStack base, Collection<ItemStack> excluded) {
        super(Stream.of(new SingleItemList(base.copy())));

        if (ItemHelpers.isEmpty(base)) {
            throw new IllegalArgumentException("Base ingredient cannot be empty");
        }

        this.base = base.copy();
        this.excluded = excluded.stream()
            .filter(stack -> !ItemHelpers.isEmpty(stack))
            .map(ItemStack::copy)
            .toArray(ItemStack[]::new);
    }

    public static ExcludedIngredient of(ItemStack base, Collection<ItemStack> excluded) {
        return new ExcludedIngredient(base, excluded);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) return false;
        if (!ItemHelpers.areItemsEqual(stack, base)) return false;

        return Arrays.stream(excluded)
            .noneMatch(excluded -> ItemHelpers.areItemsEqual(stack, excluded));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "okcore:excluded");
        json.add("base", serializeStack(base));

        JsonArray excludedArray = new JsonArray();

        for (ItemStack stack : excluded) {
            excludedArray.add(serializeStack(stack));
        }

        json.add("excluded", excludedArray);
        return json;
    }

    private static JsonObject serializeStack(ItemStack stack) {
        JsonObject json = new JsonObject();

        String itemName = Item.itemRegistry.getNameForObject(ItemHelpers.getItem(stack));
        json.addProperty("item", itemName != null ? itemName : "");
        json.addProperty("count", stack.stackSize);
        json.addProperty("data", ItemHelpers.getStackMeta(stack));
        if (ItemHelpers.hasTagCompound(stack)) {
            json.addProperty(
                "nbt",
                ItemHelpers.getTagCompound(stack)
                    .toString());
        }

        return json;
    }

    public static class Serializer implements IIngredientSerializer<ExcludedIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ExcludedIngredient fromNetwork(ExtendedBuffer buffer) {
            try {
                ItemStack base = buffer.readItemStackFromBuffer();

                int size = buffer.readVarIntFromBuffer();
                ItemStack[] excluded = new ItemStack[size];

                for (int i = 0; i < size; i++) {
                    excluded[i] = buffer.readItemStackFromBuffer();
                }

                return new ExcludedIngredient(base, Arrays.asList(excluded));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public ExcludedIngredient fromJson(JsonObject json) {
            ItemStack base = RecipeRegistry.getItemStack(json.getAsJsonObject("base"), true);

            JsonArray array = json.getAsJsonArray("excluded");
            ItemStack[] excluded = new ItemStack[array.size()];

            for (int i = 0; i < array.size(); i++) {
                excluded[i] = RecipeRegistry.getItemStack(
                    array.get(i)
                        .getAsJsonObject(),
                    true);
            }

            return new ExcludedIngredient(base, Arrays.asList(excluded));
        }

        @Override
        public void toNetwork(ExtendedBuffer buffer, ExcludedIngredient ingredient) {
            try {
                buffer.writeItemStackToBuffer(ingredient.base);
                buffer.writeVarIntToBuffer(ingredient.excluded.length);

                for (ItemStack stack : ingredient.excluded) {
                    buffer.writeItemStackToBuffer(stack);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
