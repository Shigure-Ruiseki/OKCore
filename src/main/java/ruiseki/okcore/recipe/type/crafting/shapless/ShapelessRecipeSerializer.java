package ruiseki.okcore.recipe.type.crafting.shapless;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.json.item.ItemMaterial;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.RecipeSerializerBase;

public class ShapelessRecipeSerializer extends RecipeSerializerBase<ShapelessRecipe> {

    public final static ShapelessRecipeSerializer INSTANCE = new ShapelessRecipeSerializer();

    @Override
    protected ShapelessRecipe readWithCondition(ResourceLocation id, JsonObject json) {
        ItemStack outputStack = null;

        if (json.has("result") && json.get("result")
            .isJsonObject()) {
            ItemMaterial mat = new ItemMaterial();
            mat.read(json.getAsJsonObject("result"));
            outputStack = mat.toStack();
        }

        if (outputStack == null) {
            OKCore.okLog(
                Level.ERROR,
                "Shapeless Recipe [{}] failed to generate: 'result' is missing, invalid, or the output item is not yet registered.",
                id);
            return null;
        }

        List<CompoundItemMaterial> ingredientsList = new ArrayList<>();
        if (json.has("ingredients") && json.get("ingredients")
            .isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("ingredients")) {
                CompoundItemMaterial compMaterial = new CompoundItemMaterial();
                compMaterial.read(element);

                if (compMaterial.validate()) {
                    ingredientsList.add(compMaterial);
                } else {
                    OKCore
                        .okLog(Level.ERROR, "Shapeless Recipe [{}] contains an invalid or unparseable ingredient.", id);
                    return null;
                }
            }
        } else {
            OKCore.okLog(Level.ERROR, "Shapeless Recipe [{}] is missing a valid 'ingredients' array.", id);
            return null;
        }

        if (ingredientsList.isEmpty() || ingredientsList.size() > 9) {
            OKCore.okLog(
                Level.ERROR,
                "Shapeless Recipe [{}] must have between 1 and 9 ingredients. Parsed count: {}",
                id,
                ingredientsList.size());
            return null;
        }

        return new ShapelessRecipe(id, outputStack, ingredientsList);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, ShapelessRecipe recipe) throws IOException {
        buffer.writeItemStackToBuffer(recipe.getRecipeOutput());

        List<CompoundItemMaterial> inputs = recipe.getIngredients();
        buffer.writeInt(inputs.size());

        for (CompoundItemMaterial ingredient : inputs) {
            ingredient.toNetwork(buffer);
        }
    }

    @Override
    public @Nullable ShapelessRecipe fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        ItemStack output = buffer.readItemStackFromBuffer();

        int slotsSize = buffer.readInt();
        List<CompoundItemMaterial> inputs = new ArrayList<>();

        for (int i = 0; i < slotsSize; i++) {
            CompoundItemMaterial compMaterial = new CompoundItemMaterial();
            compMaterial.fromNetwork(buffer);
            inputs.add(compMaterial);
        }

        return new ShapelessRecipe(id, output, inputs);
    }
}
