package ruiseki.okcore.data.loader.recipes;

import net.minecraft.item.crafting.IRecipe;

public interface IRecipeType<T extends IRecipe> {

    String getTypeKey();

    default boolean shouldRegisterType() {
        return true;
    }
}
