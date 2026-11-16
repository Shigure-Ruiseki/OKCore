package ruiseki.okcore.modcompat.efr.recipe.type.cooking.smoker;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.RecipeTypeConfig;
import ruiseki.okcore.recipe.IRecipeType;

public class RecipeTypeSmokerConfig extends RecipeTypeConfig<SmokerRecipe> {

    /**
     * The unique instance.
     */
    public static RecipeTypeSmokerConfig _instance;

    public RecipeTypeSmokerConfig() {
        super(OKCore._instance, true, "smoker", null, config -> new IRecipeType<SmokerRecipe>() {});
    }
}
