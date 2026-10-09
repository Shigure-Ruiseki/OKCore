package ruiseki.okcore.modcompat.jfmuy;

import java.util.Collection;
import java.util.Map;

import net.minecraft.inventory.IInventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.okcore.helper.CraftingHelpers;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;

/**
 * A base implementation of a recipe-based JFMUY recipe wrapper.
 * This caches all created recipe wrappers so they can be reused (or removed).
 *
 * @param <C> The type of the recipe container.
 * @param <R> The type of the recipe instance.
 * @author rubensworks
 */
public abstract class RecipeRegistryJFMUYRecipeWrapper<C extends IInventory, R extends IRecipeOK<C>, J extends RecipeRegistryJFMUYRecipeWrapper<C, R, J>> {

    private static final Map<IRecipeOK<?>, RecipeRegistryJFMUYRecipeWrapper<?, ?, ?>> RECIPE_WRAPPERS = Maps
        .newIdentityHashMap();

    protected final R recipe;

    protected RecipeRegistryJFMUYRecipeWrapper(IRecipeType<R> recipeType, R recipe) {
        this.recipe = recipe;
    }

    public R getRecipe() {
        return recipe;
    }

    protected abstract IRecipeType<R> getRecipeType();

    protected abstract J newInstance(R input);

    @SuppressWarnings("unchecked")
    public static <C extends IInventory, R extends IRecipeOK<C>, J extends RecipeRegistryJFMUYRecipeWrapper<C, R, J>> J getJeiRecipeWrapper(
        R input) {
        return (J) RECIPE_WRAPPERS.get(input);
    }

    public Collection<J> createAllRecipes() {
        Collection<J> wrappers = Lists.newArrayList();

        for (R input : CraftingHelpers.getClientRecipes(getRecipeType())) {
            @SuppressWarnings("unchecked")
            J wrapper = (J) RECIPE_WRAPPERS.get(input);

            if (wrapper == null) {
                wrapper = newInstance(input);
                RECIPE_WRAPPERS.put(input, wrapper);
            }

            wrappers.add(wrapper);
        }

        return wrappers;
    }

}
