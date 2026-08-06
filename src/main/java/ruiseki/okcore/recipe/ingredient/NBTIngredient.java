package ruiseki.okcore.recipe.ingredient;

import java.io.IOException;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.RecipeRegistry;

public class NBTIngredient extends Ingredient {

    private final ItemStack stack;

    public NBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.SingleItemList(stack)));
        this.stack = stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input) {
        if (input == null) return false;

        if (this.stack.getItem() != input.getItem() || this.stack.getItemDamage() != input.getItemDamage()) {
            return false;
        }

        NBTTagCompound tag1 = this.stack.getTagCompound();
        NBTTagCompound tag2 = input.getTagCompound();

        if (tag1 == tag2) return true;
        if (tag1 == null || tag2 == null) return false;

        return tag1.equals(tag2);
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
        json.addProperty(
            "type",
            RecipeRegistry.getID(Serializer.INSTANCE)
                .toString());

        String itemName = Item.itemRegistry.getNameForObject(stack.getItem());
        json.addProperty("item", itemName != null ? itemName : "");

        json.addProperty("count", stack.stackSize);

        if (stack.hasTagCompound()) {
            json.addProperty(
                "nbt",
                stack.getTagCompound()
                    .toString());
        }

        return json;
    }

    public static class Serializer implements IIngredientSerializer<NBTIngredient> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public NBTIngredient fromNetwork(ExtendedBuffer buffer) {
            try {
                return new NBTIngredient(buffer.readItemStackFromBuffer());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public NBTIngredient fromJson(JsonObject json) {
            return new NBTIngredient(RecipeRegistry.getItemStack(json, true));
        }

        @Override
        public void toNetwork(ExtendedBuffer buffer, NBTIngredient ingredient) {
            try {
                buffer.writeItemStackToBuffer(ingredient.stack);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
