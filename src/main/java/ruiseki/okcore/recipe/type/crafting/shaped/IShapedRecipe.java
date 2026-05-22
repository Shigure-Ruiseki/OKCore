package ruiseki.okcore.recipe.type.crafting.shaped;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;

import net.minecraft.inventory.IInventory;

import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeRegistry;

public interface IShapedRecipe<C extends IInventory> extends IRecipeOK<C> {

    int getRecipeWidth();

    int getRecipeHeight();

    @Override
    default IRecipeType<?> getType() {
        return RecipeRegistry.getType(SHAPED);
    }
}
