package ruiseki.okcore.recipe.type;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;

/**
 * Recipe serializer for predefined output items.
 * 
 * @author rubensworks
 */
public class RecipeSerializerCraftingShapedCustomOutput implements IRecipeSerializer<RecipeCraftingShapedCustomOutput> {

    private final Supplier<ItemStack> outputProvider;
    @Nullable
    private final IOutputTransformer outputTransformer;

    public RecipeSerializerCraftingShapedCustomOutput(Supplier<ItemStack> outputProvider,
        @Nullable IOutputTransformer outputTransformer) {
        this.outputProvider = outputProvider;
        this.outputTransformer = outputTransformer;
    }

    public RecipeSerializerCraftingShapedCustomOutput(Supplier<ItemStack> outputProvider) {
        this(outputProvider, null);
    }

    @Nullable
    public IOutputTransformer getOutputTransformer() {
        return outputTransformer;
    }

    // Partially copied from ShapedRecipe.Serializer

    @Override
    public RecipeCraftingShapedCustomOutput fromJson(ResourceLocation recipeId, JsonObject json) {
        Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelpers.getAsJsonObject(json, "key"));
        String[] astring = ShapedRecipe
            .shrink(ShapedRecipe.patternFromJson(GsonHelpers.getAsJsonArray(json, "pattern")));
        int i = astring[0].length();
        int j = astring.length;
        NonNullList<Ingredient> nonnulllist = ShapedRecipe.dissolvePattern(astring, map, i, j);
        ItemStack itemstack = this.outputProvider.get(); // This line is different
        return new RecipeCraftingShapedCustomOutput(this, recipeId, i, j, nonnulllist, itemstack);
    }

    @Override
    public RecipeCraftingShapedCustomOutput fromNetwork(ResourceLocation recipeId, ExtendedBuffer buffer)
        throws IOException {
        int i = buffer.readVarIntFromBuffer();
        int j = buffer.readVarIntFromBuffer();
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

        for (int k = 0; k < nonnulllist.size(); ++k) {
            nonnulllist.set(k, Ingredient.fromNetwork(buffer));
        }

        ItemStack itemstack = buffer.readItemStackFromBuffer();
        return new RecipeCraftingShapedCustomOutput(this, recipeId, i, j, nonnulllist, itemstack);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, RecipeCraftingShapedCustomOutput recipe) throws IOException {
        buffer.writeVarIntToBuffer(recipe.getRecipeWidth());
        buffer.writeVarIntToBuffer(recipe.getRecipeHeight());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buffer);
        }

        buffer.writeItemStackToBuffer(recipe.getRecipeOutput());
    }

    public static interface IOutputTransformer {

        public ItemStack transform(InventoryCrafting inventory, ItemStack staticOutput);
    }
}
