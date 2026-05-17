package ruiseki.okcore.data.loader.recipes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.item.ItemMaterial;

@RecipeType("minecraft:crafting_shaped")
public class ShapedRecipeMaterial extends AbstractRecipeMaterial {

    private String[] pattern;
    private Map<String, JsonElement> key;

    @Override
    protected void readSpecific(JsonObject json) {
        this.pattern = getStringArray(json, "pattern");
        this.key = new HashMap<>();
        if (json.has("key") && json.get("key")
            .isJsonObject()) {
            JsonObject keyObj = json.getAsJsonObject("key");
            for (Map.Entry<String, JsonElement> entry : keyObj.entrySet()) {
                JsonElement value = entry.getValue();
                if (!entry.getKey()
                    .isEmpty() && (value.isJsonObject() || value.isJsonArray())) {
                    this.key.put(entry.getKey(), value);
                }
            }
        }
        captureUnknownProperties(json, "type", "category", "group", "result", "pattern", "key");
    }

    @Override
    protected List<IRecipe> getRecipes() {
        List<IRecipe> recipeList = new ArrayList<>();
        ItemStack outputStack = this.result != null ? this.result.toStack() : null;
        if (outputStack == null) return recipeList;

        Set<Character> charsInPattern = new HashSet<>();
        for (String row : this.pattern) {
            if (row == null) continue;
            for (char c : row.toCharArray()) {
                if (c != ' ') {
                    charsInPattern.add(c);
                }
            }
        }

        Map<Character, List<Object>> resolvedKeys = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : this.key.entrySet()) {
            String symbolStr = entry.getKey();
            if (symbolStr == null || symbolStr.isEmpty()) continue;

            char symbolChar = symbolStr.charAt(0);

            if (!charsInPattern.contains(symbolChar)) continue;

            JsonElement element = entry.getValue();
            List<Object> rawIngredients = new ArrayList<>();

            if (element.isJsonObject()) {
                resolveIngredient(element.getAsJsonObject(), rawIngredients);
            } else if (element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement subElement : array) {
                    if (subElement.isJsonObject()) {
                        resolveIngredient(subElement.getAsJsonObject(), rawIngredients);
                    }
                }
            }

            if (rawIngredients.isEmpty()) {
                return new ArrayList<>();
            }

            resolvedKeys.put(symbolChar, rawIngredients);
            charsInPattern.remove(symbolChar);
        }

        if (!charsInPattern.isEmpty()) {
            logValidationError("Pattern contains characters that are not defined in 'key': " + charsInPattern);
            return new ArrayList<>();
        }

        List<Character> keysList = new ArrayList<>(resolvedKeys.keySet());
        List<Map<Character, Object>> combinations = new ArrayList<>();
        generateCombinations(resolvedKeys, keysList, 0, new HashMap<>(), combinations);

        for (Map<Character, Object> combo : combinations) {
            int argsLength = this.pattern.length + (combo.size() * 2);
            Object[] recipeArgs = new Object[argsLength];

            int idx = 0;
            for (String row : this.pattern) {
                recipeArgs[idx++] = row;
            }

            for (Map.Entry<Character, Object> ingredientEntry : combo.entrySet()) {
                recipeArgs[idx++] = ingredientEntry.getKey();
                recipeArgs[idx++] = ingredientEntry.getValue();
            }

            try {
                ShapedOreRecipe recipe = new ShapedOreRecipe(outputStack, recipeArgs);
                recipeList.add(recipe);
            } catch (Throwable t) {

            }
        }

        return recipeList;
    }

    private void resolveIngredient(JsonObject jsonObject, List<Object> targetList) {
        ItemMaterial mat = new ItemMaterial();
        mat.read(jsonObject);

        if (!mat.validate()) return;

        if (mat.ore != null && !mat.ore.isEmpty()) {
            String targetOre = mat.ore.trim();
            if (!OreDictionary.getOres(targetOre)
                .isEmpty()) {
                targetList.add(targetOre);
            }
        } else if (mat.item != null && !mat.item.isEmpty()) {
            int originalAmount = mat.amount;
            mat.amount = 1;
            ItemStack stack = mat.toStack();
            mat.amount = originalAmount;

            if (stack != null) {
                targetList.add(stack);
            }
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
    protected boolean validateSpecific() {
        if (pattern == null || pattern.length == 0) {
            logValidationError("Shaped recipe pattern is missing or invalid!");
            return false;
        }
        if (key == null || key.isEmpty()) {
            logValidationError("Shaped recipe key mappings are missing!");
            return false;
        }

        for (Map.Entry<String, JsonElement> entry : key.entrySet()) {
            JsonElement element = entry.getValue();
            if (element.isJsonObject()) {
                ItemMaterial mat = new ItemMaterial();
                mat.read(element.getAsJsonObject());
                if (!mat.validate()) {
                    logValidationError("Invalid ItemMaterial structure inside key token: " + entry.getKey());
                    return false;
                }
            } else if (element.isJsonArray()) {
                for (JsonElement subElement : element.getAsJsonArray()) {
                    if (!subElement.isJsonObject()) {
                        logValidationError(
                            "Array elements inside key token must be JsonObjects! Token: " + entry.getKey());
                        return false;
                    }
                    ItemMaterial mat = new ItemMaterial();
                    mat.read(subElement.getAsJsonObject());
                    if (!mat.validate()) {
                        logValidationError(
                            "Invalid ItemMaterial structure inside Array of key token: " + entry.getKey());
                        return false;
                    }
                }
            }
        }
        return super.validateSpecific();
    }

    public String[] getPattern() {
        return pattern;
    }

    public Map<String, JsonElement> getKey() {
        return key;
    }
}
