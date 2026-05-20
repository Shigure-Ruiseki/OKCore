package ruiseki.okcore.recipe.type.crafting.shapless;

import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

import net.minecraft.inventory.IInventory;

import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeRegistry;

public interface IShapelessRecipe<T extends IInventory> extends IRecipeOK<T> {

    @Override
    default IRecipeType<?> getType() {
        return RecipeRegistry.getType(SHAPELESS);
    }
}
