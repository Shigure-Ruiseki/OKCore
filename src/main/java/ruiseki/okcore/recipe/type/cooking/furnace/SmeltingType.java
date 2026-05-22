package ruiseki.okcore.recipe.type.cooking.furnace;

import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeData;

@RecipeData
public class SmeltingType implements IRecipeType<SmeltingRecipe> {

    public static final String SMELTING = "minecraft:smelting";

    @Override
    public String getTypeKey() {
        return SMELTING;
    }
}
