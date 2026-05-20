package ruiseki.okcore.recipe;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;

public interface IRecipeType<T extends IRecipeOK> {

    String getTypeKey();

    default boolean shouldRegisterType() {
        return true;
    }

    default boolean isForgeRecipe() {
        return false;
    }

    default RecipeSorter.Category getSorterCategory() {
        return RecipeSorter.Category.SHAPELESS;
    }

    default String getSorterDependencies() {
        return "after:minecraft:shapeless";
    }

    default Class<T> getRecipeClass() {
        return null;
    }

    default <C extends IInventory> Optional<T> tryMatch(IRecipeOK<C> recipeOK, World world, C inventory) {
        return recipeOK.matches(inventory, world) ? Optional.of((T) recipeOK) : Optional.empty();
    }
}
