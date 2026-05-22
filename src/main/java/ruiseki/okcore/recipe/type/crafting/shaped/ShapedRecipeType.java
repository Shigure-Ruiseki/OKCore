package ruiseki.okcore.recipe.type.crafting.shaped;

import net.minecraftforge.oredict.RecipeSorter.Category;

import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeData;

@RecipeData
public class ShapedRecipeType implements IRecipeType<ShapedRecipe> {

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
    public Class<ShapedRecipe> getRecipeClass() {
        return ShapedRecipe.class;
    }

    @Override
    public String getSorterDependencies() {
        return "after:forge:shaped";
    }
}
