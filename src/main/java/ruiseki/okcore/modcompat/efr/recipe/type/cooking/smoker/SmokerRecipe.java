package ruiseki.okcore.modcompat.efr.recipe.type.cooking.smoker;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ganymedes01.etfuturum.ModBlocks;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.cooking.AbstractCookingRecipe;

public class SmokerRecipe extends AbstractCookingRecipe {

    public SmokerRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, float experience,
        int cookingTime) {
        super(RecipeTypeSmokerConfig._instance.getInstance(), id, ingredient, result, experience, cookingTime);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.SMOKER.get());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSmokerConfig._instance.getInstance();
    }
}
