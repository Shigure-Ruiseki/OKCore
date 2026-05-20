package ruiseki.okcore.recipe;

import java.util.Collections;
import java.util.List;

import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

import ruiseki.okcore.data.loader.condition.LoadRegistry;

public abstract class RecipeSerializerBase<T extends IRecipeOK<?>> implements IRecipeSerializer<T> {

    @Override
    public final List<T> fromJson(ResourceLocation id, JsonObject json) {
        if (json.has(LoadRegistry.CONDITION_KEY)) {
            if (!LoadRegistry.checkConditional(id, json)) {
                return Collections.emptyList();
            }
        }

        List<T> recipe = this.readWithCondition(id, json);

        return recipe != null ? recipe : Collections.emptyList();
    }

    protected abstract List<T> readWithCondition(ResourceLocation id, JsonObject json);
}
