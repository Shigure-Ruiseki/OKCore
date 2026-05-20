package ruiseki.okcore.recipe.type.other;

import static ruiseki.okcore.recipe.type.other.NoneRecipeType.NONE;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeData;

@RecipeData
public class NoneRecipeSerializer implements IRecipeSerializer<IRecipeOK<?>> {

    public static final String NONE_RECIPE = "okcore:none";

    @Override
    public String getTypeKey() {
        return NONE;
    }

    @Override
    public List<IRecipeOK<?>> fromJson(ResourceLocation id, JsonObject json) {
        return Collections.singletonList(new NoneRecipe(id));
    }

    @Override
    public @Nullable IRecipeOK<?> fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        return new NoneRecipe(id);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, IRecipeOK<?> iRecipes) throws IOException {

    }
}
