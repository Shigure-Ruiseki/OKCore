package ruiseki.okcore.recipe.type.other;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;

public class NoneRecipeSerializer implements IRecipeSerializer<NoneRecipe> {

    public final static NoneRecipeSerializer INSTANCE = new NoneRecipeSerializer();

    @Override
    public NoneRecipe fromJson(ResourceLocation id, JsonObject json) {
        return new NoneRecipe(id);
    }

    @Override
    public @Nullable NoneRecipe fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        return null;
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, NoneRecipe iRecipes) throws IOException {

    }
}
