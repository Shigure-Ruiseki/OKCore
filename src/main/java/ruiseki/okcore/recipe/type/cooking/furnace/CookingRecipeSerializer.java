package ruiseki.okcore.recipe.type.cooking.furnace;

import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.cooking.AbstractCookingRecipe;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;

public class CookingRecipeSerializer<T extends AbstractCookingRecipe> implements IRecipeSerializer<T> {

    private final int defaultCookingTime;
    private final CookingRecipeSerializer.IFactory<T> factory;

    public CookingRecipeSerializer(IFactory<T> factory, int cookTime) {
        this.defaultCookingTime = cookTime;
        this.factory = factory;
    }

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
        JsonElement jsonelement = GsonHelpers.isArrayNode(json, "ingredient")
            ? GsonHelpers.getAsJsonArray(json, "ingredient")
            : GsonHelpers.getAsJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonelement);

        if (!json.has("result")) throw new JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack itemstack;
        if (json.get("result")
            .isJsonObject()) {
            itemstack = ShapedRecipe.itemFromJson(GsonHelpers.getAsJsonObject(json, "result"));
        } else {
            String s1 = GsonHelpers.getAsString(json, "result");
            Item item = GameData.getItemRegistry()
                .getObject(s1);
            if (item == null) throw new JsonSyntaxException("Item: " + s1 + " does not exist");
            itemstack = new ItemStack(item, 1, 0);
        }

        float f = GsonHelpers.getAsFloat(json, "experience", 0.0F);
        int i = GsonHelpers.getAsInt(json, "cookingtime", this.defaultCookingTime);
        return this.factory.create(id, ingredient, itemstack, f, i);
    }

    @Override
    public @Nullable T fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        ItemStack itemstack = buffer.readItemStackFromBuffer();
        float f = buffer.readFloat();
        int i = buffer.readVarIntFromBuffer();
        return this.factory.create(id, ingredient, itemstack, f, i);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, T iRecipes) throws IOException {
        iRecipes.getIngredient()
            .toNetwork(buffer);
        buffer.writeItemStackToBuffer(iRecipes.getResultItem());
        buffer.writeFloat(iRecipes.getExperience());
        buffer.writeVarIntToBuffer(iRecipes.getCookingTime());

    }

    public interface IFactory<T extends AbstractCookingRecipe> {

        T create(ResourceLocation id, Ingredient ingredient, ItemStack result, float experience, int cookingTime);
    }
}
