package ruiseki.okcore.recipe.type;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;

/**
 * @author rubensworks
 */
public class RecipeCraftingShapedCustomOutput extends ShapedRecipe {

    private final RecipeSerializerCraftingShapedCustomOutput serializer;

    public RecipeCraftingShapedCustomOutput(RecipeSerializerCraftingShapedCustomOutput serializer,
        ResourceLocation idIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn,
        ItemStack recipeOutputIn) {
        super(idIn, recipeWidthIn, recipeHeightIn, recipeItemsIn, recipeOutputIn);
        this.serializer = serializer;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public ItemStack assemble(InventoryCrafting inv) {
        RecipeSerializerCraftingShapedCustomOutput.IOutputTransformer outputTransformer = serializer
            .getOutputTransformer();
        if (outputTransformer != null) {
            return outputTransformer.transform(inv, super.getResultItem());
        }
        return super.getResultItem().copy();
    }
}
