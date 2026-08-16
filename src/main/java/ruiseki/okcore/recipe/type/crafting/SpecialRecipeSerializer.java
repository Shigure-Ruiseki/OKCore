package ruiseki.okcore.recipe.type.crafting;

import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeSerializer;

public class SpecialRecipeSerializer<T extends IRecipeOK<?>> implements IRecipeSerializer<T> {

    private final Function<ResourceLocation, T> constructor;

    public SpecialRecipeSerializer(Function<ResourceLocation, T> constructor) {
        this.constructor = constructor;
    }

    public T fromJson(ResourceLocation id, JsonObject json) {
        return this.constructor.apply(id);
    }

    public T fromNetwork(ResourceLocation id, ExtendedBuffer buffer) {
        return this.constructor.apply(id);
    }

    public void toNetwork(ExtendedBuffer buffer, T recipe) {}
}
