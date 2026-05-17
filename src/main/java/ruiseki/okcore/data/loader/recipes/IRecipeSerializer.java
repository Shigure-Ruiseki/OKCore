package ruiseki.okcore.data.loader.recipes;

import java.util.List;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import com.google.gson.JsonObject;

public interface IRecipeSerializer<T extends IRecipe> {

    String getTypeKey();

    void fromJson(ResourceLocation id, JsonObject json);

    List<T> getRecipes();

    boolean validate();

    default boolean shouldRegisterSerializer() {
        return true;
    }
}
