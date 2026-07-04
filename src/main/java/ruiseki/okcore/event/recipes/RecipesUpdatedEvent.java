package ruiseki.okcore.event.recipes;

import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.ApiStatus;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.recipe.RecipeManager;

/**
 * Fired when the {@link RecipeManager} has received and synced the recipes from the server to the client.
 *
 * <p>
 * This event is not {@linkplain Cancelable cancellable}, and does not {@linkplain HasResult have a result}.
 * </p>
 *
 * <p>
 * This event is fired on the {@linkplain MinecraftForge#EVENT_BUS main Forge event bus}
 */
public class RecipesUpdatedEvent extends Event {

    private final RecipeManager recipeManager;

    @ApiStatus.Internal
    public RecipesUpdatedEvent(RecipeManager recipeManager) {
        this.recipeManager = recipeManager;
    }

    /**
     * {@return the recipe manager}
     */
    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}
