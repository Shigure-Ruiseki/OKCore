package ruiseki.okcore.recipe;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import ruiseki.okcore.data.condition.LoadRegistry;

public abstract class RecipeSerializerBase<T extends IRecipeOK<?>> implements IRecipeSerializer<T> {

    @Override
    public final T fromJson(ResourceLocation id, JsonObject json) {
        if (json.has(LoadRegistry.CONDITION_KEY)) {
            if (!LoadRegistry.checkConditional(id, json)) {
                return null;
            }
        }

        return this.readWithCondition(id, json);
    }

    protected abstract T readWithCondition(ResourceLocation id, JsonObject json);
}
