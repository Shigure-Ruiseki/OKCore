package ruiseki.okcore.recipe.type.cooking.fuel;

import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeData;

@RecipeData
public class FuelType implements IRecipeType<FuelRecipe> {

    public static final String FUEL = "minecraft:fuel";

    @Override
    public String getTypeKey() {
        return FUEL;
    }
}
