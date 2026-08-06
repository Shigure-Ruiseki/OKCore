package ruiseki.okcore.recipe.ingredient;

import com.google.gson.JsonObject;

import ruiseki.okcore.network.ExtendedBuffer;

public interface IIngredientSerializer<T extends Ingredient> {

    T fromNetwork(ExtendedBuffer buffer);

    T fromJson(JsonObject json);

    void toNetwork(ExtendedBuffer buffer, T ingredient);
}
