package ruiseki.okcore.recipe.type.crafting.shapless;

import net.minecraftforge.oredict.RecipeSorter.Category;

import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeData;

@RecipeData
public class ShapelessRecipeType implements IRecipeType<ShapelessRecipe> {

    public static final String SHAPELESS = "minecraft:crafting_shapeless";

    @Override
    public String getTypeKey() {
        return SHAPELESS;
    }

    @Override
    public boolean isForgeRecipe() {
        return true;
    }

    @Override
    public Category getSorterCategory() {
        return Category.SHAPELESS;
    }

    @Override
    public Class<ShapelessRecipe> getRecipeClass() {
        return ShapelessRecipe.class;
    }

    @Override
    public String getSorterDependencies() {
        return "after:forge:shapeless";
    }
}
