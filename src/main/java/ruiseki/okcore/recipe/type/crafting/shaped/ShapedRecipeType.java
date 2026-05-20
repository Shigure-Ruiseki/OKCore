package ruiseki.okcore.recipe.type.crafting.shaped;

import net.minecraftforge.oredict.RecipeSorter.Category;

import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeData;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipesOK;

@RecipeData
public class ShapedRecipeType implements IRecipeType<ShapelessRecipesOK> {

    public static final String SHAPED = "minecraft:crafting_shaped";

    @Override
    public String getTypeKey() {
        return SHAPED;
    }

    @Override
    public boolean isForgeRecipe() {
        return true;
    }

    @Override
    public Category getSorterCategory() {
        return Category.SHAPED;
    }

    @Override
    public Class<ShapelessRecipesOK> getRecipeClass() {
        return ShapelessRecipesOK.class;
    }

    @Override
    public String getSorterDependencies() {
        return "after:forge:shaped";
    }
}
