package ruiseki.okcore.recipe.type.other;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeData;

@RecipeData
public class NoneRecipeType implements IRecipeType<IRecipeOK<?>> {

    public static final String NONE = "okcore:none";

    @Override
    public String getTypeKey() {
        return NONE;
    }

    @Override
    public <C extends IInventory> Optional<IRecipeOK<?>> tryMatch(IRecipeOK<C> recipeOK, World world, C inventory) {
        return Optional.empty();
    }
}
