package ruiseki.okcore.recipe.type.crafting.shaped;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.AbstractJsonMaterial;
import ruiseki.okcore.json.item.ItemMaterial;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.RecipeData;
import ruiseki.okcore.recipe.RecipeSerializerBase;

@RecipeData
public class ShapedRecipeSerializer extends RecipeSerializerBase<ShapedRecipesOK> {

    public static final String SHAPED_RECIPE = "minecraft:crafting_shaped";

    @Override
    public String getTypeKey() {
        return SHAPED_RECIPE;
    }

    @Override
    protected List<ShapedRecipesOK> readWithCondition(ResourceLocation id, JsonObject json) {
        List<ShapedRecipesOK> recipeList = new ArrayList<>();

        ItemStack outputStack = null;
        if (json.has("result")) {
            ItemMaterial mat = new ItemMaterial();
            mat.read(json.getAsJsonObject("result"));
            outputStack = mat.toStack();
        }

        if (outputStack == null) {
            OKCore.okLog(Level.ERROR, "Shaped Recipe [{}] failed to generate: 'result' is missing or invalid.", id);
            return Collections.emptyList();
        }

        String[] pattern = AbstractJsonMaterial.getStringArray(json, "pattern");
        if (pattern.length == 0) {
            OKCore.okLog(Level.ERROR, "Shaped Recipe [{}] failed to generate: 'pattern' is missing or empty.", id);
            return Collections.emptyList();
        }

        Map<Character, List<Object>> resolvedKeys = new HashMap<>();
        if (json.has("key") && json.get("key")
            .isJsonObject()) {
            JsonObject keyObj = json.getAsJsonObject("key");
            for (Map.Entry<String, JsonElement> entry : keyObj.entrySet()) {
                char symbol = entry.getKey()
                    .charAt(0);
                List<Object> rawIngredients = new ArrayList<>();
                resolveIngredients(entry.getValue(), rawIngredients);
                if (rawIngredients.isEmpty()) {
                    OKCore.okLog(Level.WARN, "Shaped Recipe [{}] key '{}' resolved to 0 ingredients.", id, symbol);
                } else {
                    resolvedKeys.put(symbol, rawIngredients);
                }
            }
        } else {
            OKCore.okLog(Level.ERROR, "Shaped Recipe [{}] is missing a valid 'key' object.", id);
            return Collections.emptyList();
        }

        List<Character> keysList = new ArrayList<>(resolvedKeys.keySet());
        List<Map<Character, Object>> combinations = new ArrayList<>();
        generateCombinations(resolvedKeys, keysList, 0, new HashMap<>(), combinations);

        if (combinations.isEmpty()) {
            OKCore.okLog(Level.WARN, "Shaped Recipe [{}] produced 0 valid combinations.", id);
            return Collections.emptyList();
        }

        for (Map<Character, Object> combo : combinations) {
            recipeList.add(new ShapedRecipesOK(id, outputStack, pattern, combo));
        }

        return recipeList;
    }

    private void resolveIngredients(JsonElement element, List<Object> targetList) {
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

    private void generateCombinations(Map<Character, List<Object>> sourceMap, List<Character> keys, int depth,
        Map<Character, Object> current, List<Map<Character, Object>> result) {
        if (depth == keys.size()) {
            result.add(new HashMap<>(current));
            return;
        }
        char currentKey = keys.get(depth);
        List<Object> options = sourceMap.get(currentKey);
        if (options != null) {
            for (Object option : options) {
                current.put(currentKey, option);
                generateCombinations(sourceMap, keys, depth + 1, current, result);
                current.remove(currentKey);
            }
        }
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, ShapedRecipesOK recipe) throws IOException {
        buffer.writeItemStackToBuffer(recipe.getRecipeOutput());

        String[] pattern = recipe.getPattern();
        buffer.writeInt(pattern.length);
        for (String s : pattern) buffer.writeString(s);

        Map<Character, Object> keyMap = recipe.getKeyMap();
        buffer.writeInt(keyMap.size());
        for (Map.Entry<Character, Object> entry : keyMap.entrySet()) {
            buffer.writeChar(entry.getKey());
            Object val = entry.getValue();
            if (val instanceof ItemStack stack) {
                buffer.writeByte(2);
                buffer.writeItemStackToBuffer(stack);
            } else if (val instanceof String string) {
                buffer.writeByte(3);
                buffer.writeString(string);
            }
        }
    }

    @Override
    public @Nullable ShapedRecipesOK fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        ItemStack output = buffer.readItemStackFromBuffer();

        int pLen = buffer.readInt();
        String[] pattern = new String[pLen];
        for (int i = 0; i < pLen; i++) pattern[i] = buffer.readString();

        int kLen = buffer.readInt();
        Map<Character, Object> keyMap = new HashMap<>();
        for (int i = 0; i < kLen; i++) {
            char key = buffer.readChar();
            byte type = buffer.readByte();
            if (type == 2) {
                keyMap.put(key, buffer.readItemStackFromBuffer());
            } else if (type == 3) {
                keyMap.put(key, buffer.readString());
            }
        }

        return new ShapedRecipesOK(id, output, pattern, keyMap);
    }
}
