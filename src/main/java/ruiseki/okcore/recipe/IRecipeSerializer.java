package ruiseki.okcore.recipe;

import java.io.IOException;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;

public interface IRecipeSerializer<T extends IRecipeOK<?>> {

    String getTypeKey();

    default boolean shouldRegisterSerializer() {
        return true;
    }

    List<T> fromJson(ResourceLocation id, JsonObject json);

    @Nullable
    T fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException;

    void toNetwork(ExtendedBuffer buffer, T iRecipes) throws IOException;
}
