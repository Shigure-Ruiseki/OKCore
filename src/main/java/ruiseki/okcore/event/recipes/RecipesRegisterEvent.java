
package ruiseki.okcore.event.recipes;

import java.util.Map;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.eventhandler.Event;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;

/**
 * Fired when recipes can be registered, modified, or removed from
 * the recipe registry.
 *
 * <p>
 * This event provides access to the {@link RecipeManager} and the
 * underlying recipe collections, allowing subscribers to register
 * additional recipes or remove existing ones.
 * </p>
 *
 * <p>
 * Recipes are indexed in two ways:
 * </p>
 *
 * <ul>
 * <li>By {@link IRecipeType}, allowing recipes to be grouped by type.</li>
 * <li>By {@link ResourceLocation}, allowing recipes to be retrieved
 * by their unique identifier.</li>
 * </ul>
 *
 * <p>
 * Use {@link #addRecipe(IRecipeOK)} and
 * {@link #removeRecipe(ResourceLocation)} to update both indexes
 * consistently.
 * </p>
 *
 * <p>
 * The maps returned by this event are mutable references to the
 * underlying recipe collections. Direct modifications may cause
 * the two indexes to become inconsistent.
 * </p>
 *
 * <p>
 * This event is not cancelable and does not have a result.
 * </p>
 *
 * @see RecipeManager
 * @see IRecipeOK
 * @see IRecipeType
 */
public class RecipesRegisterEvent extends Event {

    private final RecipeManager recipeManager;
    private final Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> recipesByType;
    private final Map<ResourceLocation, IRecipeOK<?>> recipesByName;

    /**
     * Constructs a recipe registration event.
     *
     * @param recipeManager the recipe manager associated with this event
     * @param recipesByType the recipe registry grouped by recipe type
     * @param recipesByName the recipe registry indexed by recipe identifier
     */
    public RecipesRegisterEvent(RecipeManager recipeManager,
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> recipesByType,
        Map<ResourceLocation, IRecipeOK<?>> recipesByName) {
        this.recipeManager = recipeManager;
        this.recipesByType = recipesByType;
        this.recipesByName = recipesByName;
    }

    /**
     * Returns the recipe manager associated with this event.
     *
     * @return the active recipe manager
     */
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    /**
     * Returns the recipes grouped by their recipe types.
     *
     * <p>
     * Each recipe type maps to another map containing recipe
     * identifiers and their corresponding recipe instances.
     * </p>
     *
     * <p>
     * The returned map is mutable and represents the underlying
     * registry. Prefer {@link #addRecipe(IRecipeOK)} and
     * {@link #removeRecipe(ResourceLocation)} when modifying recipes.
     * </p>
     *
     * @return the recipe registry indexed by recipe type
     */
    public Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> getRecipesByType() {
        return recipesByType;
    }

    /**
     * Returns the recipes indexed by their unique identifiers.
     *
     * <p>
     * Each {@link ResourceLocation} is associated with an
     * {@link IRecipeOK} instance.
     * </p>
     *
     * <p>
     * The returned map is mutable and represents the underlying
     * registry. Direct modifications are not automatically reflected
     * in the type-based registry.
     * </p>
     *
     * @return the recipe registry indexed by recipe identifier
     */
    public Map<ResourceLocation, IRecipeOK<?>> getRecipesByName() {
        return recipesByName;
    }

    /**
     * Registers a recipe in both the type-based and name-based registries.
     *
     * <p>
     * The recipe is registered under its {@link IRecipeOK#getType()}
     * and {@link IRecipeOK#getId()}.
     * </p>
     *
     * <p>
     * If no registry exists for the recipe type, a new map is created.
     * If a recipe with the same identifier already exists, it is
     * replaced in the affected maps.
     * </p>
     *
     * <p>
     * Recipes that are {@code null} or have a {@code null} identifier
     * are ignored.
     * </p>
     *
     * @param recipe the recipe to register
     *
     * @see #removeRecipe(ResourceLocation)
     */
    public void addRecipe(IRecipeOK<?> recipe) {
        if (recipe != null && recipe.getId() != null) {
            this.recipesByType.computeIfAbsent(recipe.getType(), k -> new Object2ObjectOpenHashMap<>())
                .put(recipe.getId(), recipe);

            this.recipesByName.put(recipe.getId(), recipe);
        }
    }

    /**
     * Removes a recipe from the name-based and type-based registries.
     *
     * <p>
     * The recipe is first removed from the identifier-based registry.
     * If a matching recipe is found, its entry is also removed from
     * the corresponding recipe type registry.
     * </p>
     *
     * <p>
     * If no recipe exists with the specified identifier, this method
     * performs no further modifications.
     * </p>
     *
     * <p>
     * Empty recipe type maps are not automatically removed.
     * </p>
     *
     * @param id the identifier of the recipe to remove
     *
     * @see #addRecipe(IRecipeOK)
     */
    public void removeRecipe(ResourceLocation id) {
        IRecipeOK<?> removed = this.recipesByName.remove(id);

        if (removed != null && this.recipesByType.containsKey(removed.getType())) {
            this.recipesByType.get(removed.getType())
                .remove(id);
        }
    }
}
