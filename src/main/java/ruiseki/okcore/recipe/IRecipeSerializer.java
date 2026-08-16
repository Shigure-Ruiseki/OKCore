package ruiseki.okcore.recipe;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;

public interface IRecipeSerializer<T extends IRecipeOK<?>> {

    T fromJson(ResourceLocation id, JsonObject json);

    @Nullable
    T fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException;

    void toNetwork(ExtendedBuffer buffer, T iRecipes) throws IOException;

    public static <S extends IRecipeSerializer<T>, T extends IRecipeOK<?>> S register(String key, S serializer) {
        return RecipeRegistry.registerSerializer(new ResourceLocation(key), serializer);
    }
}
