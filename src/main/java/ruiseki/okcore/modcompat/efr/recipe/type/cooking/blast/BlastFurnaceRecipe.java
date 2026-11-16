package ruiseki.okcore.modcompat.efr.recipe.type.cooking.blast;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ganymedes01.etfuturum.ModBlocks;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.cooking.AbstractCookingRecipe;

public class BlastFurnaceRecipe extends AbstractCookingRecipe {

    public BlastFurnaceRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result, float experience,
        int cookingTime) {
        super(RecipeTypeBlastFurnaceConfig._instance.getInstance(), id, ingredient, result, experience, cookingTime);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.SMOKER.get());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeBlastFurnaceConfig._instance.getInstance();
    }
}
