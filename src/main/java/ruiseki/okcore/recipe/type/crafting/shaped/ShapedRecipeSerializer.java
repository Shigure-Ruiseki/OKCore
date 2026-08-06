package ruiseki.okcore.recipe.type.crafting.shaped;

import java.io.IOException;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public class ShapedRecipeSerializer implements IRecipeSerializer<ShapedRecipe> {

    @Override
    public ShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
        Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelpers.getAsJsonObject(json, "key"));
        String[] astring = ShapedRecipe
            .shrink(ShapedRecipe.patternFromJson(GsonHelpers.getAsJsonArray(json, "pattern")));
        int i = astring[0].length();
        int j = astring.length;
        NonNullList<Ingredient> nonnulllist = ShapedRecipe.dissolvePattern(astring, map, i, j);
        ItemStack itemstack = ShapedRecipe.itemFromJson(GsonHelpers.getAsJsonObject(json, "result"));

        return new ShapedRecipe(id, i, j, nonnulllist, itemstack);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, ShapedRecipe recipe) throws IOException {
        buffer.writeVarIntToBuffer(recipe.getRecipeWidth());
        buffer.writeVarIntToBuffer(recipe.getRecipeHeight());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeItemStackToBuffer(recipe.getResultItem());
    }

    @Override
    public @Nullable ShapedRecipe fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        int i = buffer.readVarIntFromBuffer();
        int j = buffer.readVarIntFromBuffer();
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

        for (int k = 0; k < nonnulllist.size(); ++k) {
            nonnulllist.set(k, Ingredient.fromNetwork(buffer));
        }

        ItemStack itemstack = buffer.readItemStackFromBuffer();
        return new ShapedRecipe(id, i, j, nonnulllist, itemstack);
    }
}
