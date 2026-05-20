package ruiseki.okcore.recipe.type.crafting.shaped;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;

import net.minecraft.inventory.IInventory;

import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeRegistries;

public interface IShapedRecipe<T extends IInventory> extends IRecipeOK<T> {

    int getRecipeWidth();

    int getRecipeHeight();

    @Override
    default IRecipeType<?> getType() {
        return RecipeRegistries.getType(SHAPED);
    }
}
