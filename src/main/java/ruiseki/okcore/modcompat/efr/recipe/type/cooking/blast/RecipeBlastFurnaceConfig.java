package ruiseki.okcore.modcompat.efr.recipe.type.cooking.blast;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.type.cooking.furnace.CookingRecipeSerializer;

public class RecipeBlastFurnaceConfig extends RecipeConfig<BlastFurnaceRecipe> {

    /**
     * The unique instance.
     */
    public static RecipeBlastFurnaceConfig _instance;

    public RecipeBlastFurnaceConfig() {
        super(
            OKCore._instance,
            true,
            "blast_furnace",
            null,
            config -> new CookingRecipeSerializer<>(BlastFurnaceRecipe::new, 100));
    }
}
