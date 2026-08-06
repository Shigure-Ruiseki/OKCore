package ruiseki.okcore.recipe.type.crafting;

import net.minecraft.inventory.InventoryCrafting;

import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeRegistry;

public interface ICraftingRecipe extends IRecipeOK<InventoryCrafting> {

    default IRecipeType<?> getType() {
        return RecipeRegistry.CRAFTING;
    }
}
