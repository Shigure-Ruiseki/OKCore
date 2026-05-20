package ruiseki.okcore.recipe.type.other;

import static ruiseki.okcore.recipe.type.other.NoneRecipeSerializer.NONE_RECIPE;
import static ruiseki.okcore.recipe.type.other.NoneRecipeType.NONE;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeDataBase;
import ruiseki.okcore.recipe.RecipeRegistry;

public class NoneRecipe extends RecipeDataBase {

    public NoneRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.getSerializer(NONE_RECIPE);
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeRegistry.getType(NONE);
    }
}
