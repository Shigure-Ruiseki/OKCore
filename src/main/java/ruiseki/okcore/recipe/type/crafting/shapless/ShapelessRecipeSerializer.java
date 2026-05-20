package ruiseki.okcore.recipe.type.crafting.shapless;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.item.ItemMaterial;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.RecipeData;
import ruiseki.okcore.recipe.RecipeSerializerBase;

@RecipeData
public class ShapelessRecipeSerializer extends RecipeSerializerBase<ShapelessRecipesOK> {

    public static final String SHAPELESS_RECIPE = "minecraft:crafting_shapeless";

    @Override
    public String getTypeKey() {
        return SHAPELESS_RECIPE;
    }

    @Override
    protected List<ShapelessRecipesOK> readWithCondition(ResourceLocation id, JsonObject json) {
        List<ShapelessRecipesOK> recipeList = new ArrayList<>();

        ItemStack outputStack = null;
        if (json.has("result")) {
            ItemMaterial mat = new ItemMaterial();
            mat.read(json.getAsJsonObject("result"));
            outputStack = mat.toStack();
        }

        if (outputStack == null) {
            OKCore.okLog(Level.ERROR, "Shapeless Recipe [{}] failed to generate: 'result' is missing or invalid.", id);
            return Collections.emptyList();
        }

        List<List<Object>> resolvedIngredientsList = new ArrayList<>();
        if (json.has("ingredients") && json.get("ingredients")
            .isJsonArray()) {
            for (JsonElement element : json.getAsJsonArray("ingredients")) {
                List<Object> rawIngredients = new ArrayList<>();
                resolveIngredientElement(element, rawIngredients);
                if (!rawIngredients.isEmpty()) {
                    resolvedIngredientsList.add(rawIngredients);
                } else {
                    OKCore.okLog(
                        Level.WARN,
                        "Shapeless Recipe [{}] has an ingredient element that resolved to nothing.",
                        id);
                }
            }
        } else {
            OKCore.okLog(Level.ERROR, "Shapeless Recipe [{}] is missing a valid 'ingredients' array.", id);
            return Collections.emptyList();
        }

        List<List<Object>> combinations = new ArrayList<>();
        generateCombinations(resolvedIngredientsList, 0, new ArrayList<>(), combinations);

        for (List<Object> combo : combinations) {
            recipeList.add(new ShapelessRecipesOK(id, outputStack, combo));
        }

        if (recipeList.isEmpty()) {
            OKCore.okLog(Level.WARN, "Shapeless Recipe [{}] generated 0 valid combinations.", id);
        }
        return recipeList;
    }

    private void resolveIngredientElement(JsonElement element, List<Object> targetList) {
        if (element.isJsonObject()) {
            resolveIngredient(element.getAsJsonObject(), targetList);
        } else if (element.isJsonArray()) {
            for (JsonElement sub : element.getAsJsonArray()) {
                if (sub.isJsonObject()) resolveIngredient(sub.getAsJsonObject(), targetList);
            }
        }
    }

    private void resolveIngredient(JsonObject jsonObject, List<Object> targetList) {
        ItemMaterial mat = new ItemMaterial();
        mat.read(jsonObject);
        if (!mat.validate()) return;
        if (mat.getOre() != null) {
            String ore = mat.getOre()
                .trim();
            if (!OreDictionary.getOres(ore)
                .isEmpty()) {
                targetList.add(ore);
            } else {
                OKCore.okLog(Level.TRACE, "Ore '{}' not found in dictionary.", ore);
            }
        } else if (mat.getItem() != null) {
            ItemStack stack = mat.toStack();
            stack.stackSize = 1;
            targetList.add(stack);
        }
    }

    private void generateCombinations(List<List<Object>> sourceList, int depth, List<Object> current,
        List<List<Object>> result) {
        if (depth == sourceList.size()) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (Object option : sourceList.get(depth)) {
            current.add(option);
            generateCombinations(sourceList, depth + 1, current, result);
            current.removeLast();
        }
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, ShapelessRecipesOK recipe) throws IOException {
        buffer.writeItemStackToBuffer(recipe.getRecipeOutput());
        List<Object> inputs = recipe.getIngredients();
        buffer.writeInt(inputs.size());
        for (Object obj : inputs) {
            if (obj instanceof ItemStack stack) {
                buffer.writeByte(1);
                buffer.writeItemStackToBuffer(stack);
            } else if (obj instanceof String string) {
                buffer.writeByte(2);
                buffer.writeString(string);
            } else if (obj instanceof List) {
                buffer.writeByte(3);
                List<ItemStack> list = (List<ItemStack>) obj;
                buffer.writeInt(list.size());
                for (ItemStack stack : list) buffer.writeItemStackToBuffer(stack);
            }
        }
    }

    @Override
    public @Nullable ShapelessRecipesOK fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        ItemStack output = buffer.readItemStackFromBuffer();
        int size = buffer.readInt();
        List<Object> inputs = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            byte type = buffer.readByte();
            if (type == 1) inputs.add(buffer.readItemStackFromBuffer());
            else if (type == 2) inputs.add(buffer.readString());
            else if (type == 3) {
                int listSize = buffer.readInt();
                List<ItemStack> list = new ArrayList<>();
                for (int k = 0; k < listSize; k++) list.add(buffer.readItemStackFromBuffer());
                inputs.add(list);
            }
        }
        return new ShapelessRecipesOK(id, output, inputs);
    }

}
