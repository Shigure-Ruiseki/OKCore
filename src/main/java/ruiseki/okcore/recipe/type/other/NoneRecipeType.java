package ruiseki.okcore.recipe.type.other;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;

public class NoneRecipeType implements IRecipeType<NoneRecipe> {

    public final static NoneRecipeType INSTANCE = new NoneRecipeType();

    @Override
    public <C extends IInventory> Optional<NoneRecipe> tryMatch(IRecipeOK<C> recipeOK, World world, C inventory) {
        return Optional.empty();
    }
}
