package ruiseki.okcore.recipe.ingredient;

import java.io.IOException;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;

public class VanillaIngredientSerializer implements IIngredientSerializer<Ingredient> {

    public static final VanillaIngredientSerializer INSTANCE = new VanillaIngredientSerializer();

    @Override
    public Ingredient fromNetwork(ExtendedBuffer buffer) {
        try {
            int size = buffer.readVarIntFromBuffer();
            if (size <= 0) {
                return Ingredient.EMPTY;
            }

            ItemStack[] stacks = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                stacks[i] = buffer.readItemStackFromBuffer();
            }

            return Ingredient.of(stacks);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read Ingredient from network buffer", e);
        }
    }

    @Override
    public Ingredient fromJson(JsonObject json) {
        return Ingredient.fromValues(Stream.of(Ingredient.valueFromJson(json)));
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        buffer.writeVarIntToBuffer(items.length);

        for (ItemStack stack : items) {
            try {
                buffer.writeItemStackToBuffer(stack);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
