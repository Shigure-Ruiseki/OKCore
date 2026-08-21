package ruiseki.okcore.recipe.type;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

/**
 * @author rubensworks
 */
public class RecipeCraftingShapelessCustomOutput extends ShapelessRecipe {

    private final RecipeSerializerCraftingShapelessCustomOutput serializer;

    public RecipeCraftingShapelessCustomOutput(RecipeSerializerCraftingShapelessCustomOutput serializer,
        ResourceLocation idIn, ItemStack recipeOutputIn, NonNullList<Ingredient> recipeItemsIn) {
        super(idIn, recipeOutputIn, recipeItemsIn);
        this.serializer = serializer;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public ItemStack assemble(InventoryCrafting inv) {
        RecipeSerializerCraftingShapelessCustomOutput.IOutputTransformer outputTransformer = serializer
            .getOutputTransformer();
        if (outputTransformer != null) {
            return outputTransformer.transform(inv, super.getResultItem());
        }
        return super.getResultItem().copy();
    }
}
