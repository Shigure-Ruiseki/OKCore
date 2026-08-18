package ruiseki.okcore.recipe.type;

import java.io.IOException;
import java.util.function.Supplier;

import net.minecraft.inventory.InventoryCrafting;
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

/**
 * Recipe serializer for predefined output items.
 * 
 * @author rubensworks
 */
public class RecipeSerializerCraftingShapelessCustomOutput
    implements IRecipeSerializer<RecipeCraftingShapelessCustomOutput> {

    private final Supplier<ItemStack> outputProvider;
    @Nullable
    private final IOutputTransformer outputTransformer;

    public RecipeSerializerCraftingShapelessCustomOutput(Supplier<ItemStack> outputProvider,
        @Nullable IOutputTransformer outputTransformer) {
        this.outputProvider = outputProvider;
        this.outputTransformer = outputTransformer;
    }

    public RecipeSerializerCraftingShapelessCustomOutput(Supplier<ItemStack> outputProvider) {
        this(outputProvider, null);
    }

    @Nullable
    public IOutputTransformer getOutputTransformer() {
        return outputTransformer;
    }

    // Partially copied from ShapelessRecipe.Serializer

    @Override
    public RecipeCraftingShapelessCustomOutput fromJson(ResourceLocation recipeId, JsonObject json) {
        NonNullList<Ingredient> nonnulllist = readIngredients(GsonHelpers.getAsJsonArray(json, "ingredients"));
        if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
        } else if (nonnulllist.size() > 3 * 3) {
            throw new JsonParseException("Too many ingredients for shapeless recipe the max is " + (3 * 3));
        } else {
            ItemStack itemstack = this.outputProvider.get(); // This line is different
            return new RecipeCraftingShapelessCustomOutput(this, recipeId, itemstack, nonnulllist);
        }
    }

    private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();

        for (int i = 0; i < ingredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
            if (!ingredient.isEmpty()) {
                nonnulllist.add(ingredient);
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeCraftingShapelessCustomOutput fromNetwork(ResourceLocation recipeId, ExtendedBuffer buffer)
        throws IOException {
        int i = buffer.readVarIntFromBuffer();
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

        for (int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.fromNetwork(buffer));
        }

        ItemStack itemstack = buffer.readItemStackFromBuffer();
        return new RecipeCraftingShapelessCustomOutput(this, recipeId, itemstack, nonnulllist);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, RecipeCraftingShapelessCustomOutput recipe) throws IOException {
        buffer.writeVarIntToBuffer(
            recipe.getIngredients()
                .size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeItemStackToBuffer(recipe.getRecipeOutput());
    }

    public static interface IOutputTransformer {

        public ItemStack transform(InventoryCrafting inventory, ItemStack staticOutput);
    }
}
