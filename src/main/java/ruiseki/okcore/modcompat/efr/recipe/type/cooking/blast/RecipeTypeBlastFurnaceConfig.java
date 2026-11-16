package ruiseki.okcore.modcompat.efr.recipe.type.cooking.blast;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.RecipeTypeConfig;
import ruiseki.okcore.recipe.IRecipeType;

public class RecipeTypeBlastFurnaceConfig extends RecipeTypeConfig<BlastFurnaceRecipe> {

    /**
     * The unique instance.
     */
    public static RecipeTypeBlastFurnaceConfig _instance;

    public RecipeTypeBlastFurnaceConfig() {
        super(OKCore._instance, true, "blast_furnace", null, config -> new IRecipeType<BlastFurnaceRecipe>() {});
    }
}
