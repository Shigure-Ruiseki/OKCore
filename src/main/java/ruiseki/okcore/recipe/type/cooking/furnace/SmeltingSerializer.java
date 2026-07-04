package ruiseki.okcore.recipe.type.cooking.furnace;

import java.io.IOException;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.json.item.ItemMaterial;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;

public class SmeltingSerializer implements IRecipeSerializer<SmeltingRecipe> {

    public static final SmeltingSerializer INSTANCE = new SmeltingSerializer();

    @Override
    public SmeltingRecipe fromJson(ResourceLocation id, JsonObject json) {
        ItemStack outputStack = null;
        if (json.has("result")) {
            ItemMaterial mat = new ItemMaterial();
            mat.read(json.get("result"));
            outputStack = mat.toStack();
        }

        if (outputStack == null) {
            OKCore.okLog(Level.ERROR, "Smelting Recipe [{}] failed to generate: 'result' is missing or invalid.", id);
            return null;
        }

        CompoundItemMaterial ingredient = new CompoundItemMaterial();
        if (json.has("ingredient")) {
            ingredient.read(json.get("ingredient"));
        } else {
            OKCore.okLog(Level.ERROR, "Smelting Recipe [{}] is missing 'ingredient'.", id);
            return null;
        }

        if (ingredient.isEmpty()) {
            OKCore.okLog(Level.WARN, "Smelting Recipe [{}] has an empty or invalid ingredient parsed.", id);
            return null;
        }

        float experience = json.has("experience") ? json.get("experience")
            .getAsFloat() : 0.0F;
        int cookingTime = json.has("cookingtime") ? json.get("cookingtime")
            .getAsInt() : 200;

        return new SmeltingRecipe(id, outputStack, ingredient, experience, cookingTime);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, SmeltingRecipe recipe) throws IOException {
        buffer.writeItemStackToBuffer(recipe.getRecipeOutput());
        recipe.getIngredient()
            .toNetwork(buffer);
        buffer.writeFloat(recipe.getExperience());
        buffer.writeInt(recipe.getCookingTime());
    }

    @Override
    public @Nullable SmeltingRecipe fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        ItemStack output = buffer.readItemStackFromBuffer();
        CompoundItemMaterial ingredient = new CompoundItemMaterial();
        ingredient.fromNetwork(buffer);
        float experience = buffer.readFloat();
        int cookingTime = buffer.readInt();
        return new SmeltingRecipe(id, output, ingredient, experience, cookingTime);
    }
}
