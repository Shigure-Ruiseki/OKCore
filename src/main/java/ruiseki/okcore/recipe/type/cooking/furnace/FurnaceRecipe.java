package ruiseki.okcore.recipe.type.cooking.furnace;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.cooking.AbstractCookingRecipe;

public class FurnaceRecipe extends AbstractCookingRecipe {

    public FurnaceRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, float experience,
        int cookingTime) {
        super(RecipeRegistry.SMELTING, id, ingredient, result, experience, cookingTime);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.furnace);
    }

    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SMELTING_RECIPE;
    }
}
