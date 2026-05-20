package ruiseki.okcore.recipe.type.other;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;

public class NoneRecipeSerializer implements IRecipeSerializer<IRecipeOK<?>> {

    @Override
    public String getTypeKey() {
        return "okcore:none";
    }

    @Override
    public List<IRecipeOK<?>> fromJson(ResourceLocation id, JsonObject json) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable IRecipeOK<?> fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        return null;
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, IRecipeOK<?> iRecipes) throws IOException {

    }
}
