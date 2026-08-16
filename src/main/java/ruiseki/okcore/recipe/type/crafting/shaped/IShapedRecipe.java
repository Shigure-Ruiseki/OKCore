package ruiseki.okcore.recipe.type.crafting.shaped;

import net.minecraft.inventory.IInventory;

import ruiseki.okcore.recipe.IRecipeOK;

public interface IShapedRecipe<C extends IInventory> extends IRecipeOK<C> {

    int getRecipeWidth();

    int getRecipeHeight();
}
