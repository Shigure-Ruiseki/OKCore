package ruiseki.okcore.data.loader.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.RecipeSorter.Category;

public interface IRecipeType<T extends IRecipe> {

    String getTypeKey();

    default boolean shouldRegisterType() {
        return true;
    }

    default boolean isForgeRecipe() {
        return false;
    }

    default Category getSorterCategory() {
        return Category.SHAPELESS;
    }

    default String getSorterDependencies() {
        return "after:minecraft:shapeless";
    }

    default Class<? extends IRecipe> getRecipeClass() {
        return null;
    }
}
