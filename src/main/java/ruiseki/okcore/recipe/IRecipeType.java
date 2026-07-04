package ruiseki.okcore.recipe;

import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public interface IRecipeType<T extends IRecipeOK<?>> {

    default <C extends IInventory> Optional<T> tryMatch(IRecipeOK<C> recipeOK, World world, C inventory) {
        return recipeOK.matchesOK(inventory, world) ? Optional.of((T) recipeOK) : Optional.empty();
    }

    public static <T extends IRecipeOK<?>> IRecipeType<T> simple(final ResourceLocation name) {
        return new IRecipeType<T>() {

            @Override
            public String toString() {
                return name.toString();
            }
        };
    }
}
