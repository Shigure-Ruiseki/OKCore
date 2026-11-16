package ruiseki.okcore.modcompat.efr.recipe.type.cooking.smoker;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.type.cooking.furnace.CookingRecipeSerializer;

public class RecipeSmokerConfig extends RecipeConfig<SmokerRecipe> {

    /**
     * The unique instance.
     */
    public static RecipeSmokerConfig _instance;

    public RecipeSmokerConfig() {
        super(
            OKCore._instance,
            true,
            "blast_furnace",
            null,
            config -> new CookingRecipeSerializer<>(SmokerRecipe::new, 100));
    }
}
