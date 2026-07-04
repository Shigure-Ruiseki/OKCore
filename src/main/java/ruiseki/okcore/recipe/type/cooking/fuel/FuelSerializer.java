package ruiseki.okcore.recipe.type.cooking.fuel;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.item.ItemMaterial;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeData;

@RecipeData
public class FuelSerializer implements IRecipeSerializer<FuelRecipe> {

    public static final FuelSerializer INSTANCE = new FuelSerializer();

    @Override
    public FuelRecipe fromJson(ResourceLocation id, JsonObject json) {
        ItemStack input = null;
        if (json.has("input")) {
            ItemMaterial mat = new ItemMaterial();
            mat.read(json.get("input"));
            input = mat.toStack();
        }

        if (input == null) {
            OKCore.okLog(Level.ERROR, "Fuel Recipe [{}] failed to generate: 'input' is missing or invalid.", id);
            return null;
        }

        int cookingTime = json.has("cookingtime") ? json.get("cookingtime")
            .getAsInt() : 200;

        return new FuelRecipe(id, input, cookingTime);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, FuelRecipe recipe) throws IOException {
        buffer.writeItemStackToBuffer(recipe.getInput());
        buffer.writeInt(recipe.getBurnTime());
    }

    @Override
    public @Nullable FuelRecipe fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        ItemStack input = buffer.readItemStackFromBuffer();
        int cookingTime = buffer.readInt();
        return new FuelRecipe(id, input, cookingTime);
    }
}
