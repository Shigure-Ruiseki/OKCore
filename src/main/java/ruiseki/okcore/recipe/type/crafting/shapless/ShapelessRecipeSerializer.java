package ruiseki.okcore.recipe.type.crafting.shapless;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;

public class ShapelessRecipeSerializer implements IRecipeSerializer<ShapelessRecipe> {

    @Override
    public ShapelessRecipe fromJson(ResourceLocation id, JsonObject json) {
        NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelpers.getAsJsonArray(json, "ingredients"));
        if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        } else if (nonnulllist.size() > ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT) {
            throw new JsonParseException(
                "Too many ingredients for shapeless recipe the max is "
                    + (ShapedRecipe.MAX_WIDTH * ShapedRecipe.MAX_HEIGHT));
        } else {
            ItemStack itemstack = ShapedRecipe.itemFromJson(GsonHelpers.getAsJsonObject(json, "result"));
            return new ShapelessRecipe(id, itemstack, nonnulllist);
        }
    }

    private static NonNullList<Ingredient> itemsFromJson(JsonArray p_199568_0_) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < p_199568_0_.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(p_199568_0_.get(i));
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);
            }
        }

        return nonnulllist;
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, ShapelessRecipe recipe) throws IOException {
        buffer.writeVarIntToBuffer(
            recipe.getIngredients()
                .size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeItemStackToBuffer(recipe.getResultItem());
    }

    @Override
    public @Nullable ShapelessRecipe fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        int i = buffer.readVarIntFromBuffer();
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

        for (int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.fromNetwork(buffer));
        }

        ItemStack itemstack = buffer.readItemStackFromBuffer();
        return new ShapelessRecipe(id, itemstack, nonnulllist);
    }
}
