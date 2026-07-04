package ruiseki.okcore.recipe.type.other;

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
    public IRecipeSerializer<NoneRecipe> getSerializer() {
        return RecipeRegistry.NONE_SERIALIZER;
    }

    @Override
    public IRecipeType<NoneRecipe> getType() {
        return RecipeRegistry.NONE_TYPE;
    }
}
