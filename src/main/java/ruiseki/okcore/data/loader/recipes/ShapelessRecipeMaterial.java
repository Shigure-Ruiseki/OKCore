package ruiseki.okcore.data.loader.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.json.item.ItemMaterial;

@RecipeType("minecraft:crafting_shapeless")
public class ShapelessRecipeMaterial extends AbstractRecipeMaterial {

    private List<JsonElement> ingredients;

    @Override
    protected void readSpecific(JsonObject json) {
        this.ingredients = new ArrayList<>();

        if (json.has("ingredients") && json.get("ingredients")
            .isJsonArray()) {
            JsonArray ingredientsArray = json.getAsJsonArray("ingredients");
            for (JsonElement element : ingredientsArray) {
                if (element.isJsonObject() || element.isJsonArray()) {
                    this.ingredients.add(element);
                }
            }
        }

        captureUnknownProperties(json, "type", "category", "group", "result", "ingredients");
    }

    @Override
    protected List<IRecipe> getRecipes() {
        List<IRecipe> recipeList = new ArrayList<>();
        ItemStack outputStack = this.result != null ? this.result.toStack() : null;
        if (outputStack == null) return recipeList;

        List<List<Object>> resolvedIngredientsList = new ArrayList<>();

        for (JsonElement element : this.ingredients) {
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

            resolvedIngredientsList.add(rawIngredients);
        }

        List<List<Object>> combinations = new ArrayList<>();
        generateCombinations(resolvedIngredientsList, 0, new ArrayList<>(), combinations);

        for (List<Object> combo : combinations) {
            try {
                ShapelessOreRecipe recipe = new ShapelessOreRecipe(outputStack, combo.toArray());
                recipeList.add(recipe);
            } catch (Throwable ignore) {}
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

    private void generateCombinations(List<List<Object>> sourceList, int depth, List<Object> current,
        List<List<Object>> result) {
        if (depth == sourceList.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        List<Object> options = sourceList.get(depth);
        if (options != null) {
            for (Object option : options) {
                current.add(option);
                generateCombinations(sourceList, depth + 1, current, result);
                current.removeLast();
            }
        }
    }

    @Override
    protected boolean validateSpecific() {
        if (this.ingredients == null || this.ingredients.isEmpty()) {
            logValidationError("Shapeless recipe ingredients list is missing or empty!");
            return false;
        }

        if (this.ingredients.size() > 9) {
            logValidationError("Shapeless recipe cannot have more than 9 ingredients!");
            return false;
        }

        for (JsonElement element : this.ingredients) {
            if (element.isJsonObject()) {
                ItemMaterial mat = new ItemMaterial();
                mat.read(element.getAsJsonObject());
                if (!mat.validate()) {
                    logValidationError("Invalid ItemMaterial structure inside ingredients");
                    return false;
                }
            } else if (element.isJsonArray()) {
                for (JsonElement subElement : element.getAsJsonArray()) {
                    if (!subElement.isJsonObject()) {
                        logValidationError("Array elements inside ingredients must be JsonObjects!");
                        return false;
                    }
                    ItemMaterial mat = new ItemMaterial();
                    mat.read(subElement.getAsJsonObject());
                    if (!mat.validate()) {
                        logValidationError("Invalid ItemMaterial structure inside Array of ingredients");
                        return false;
                    }
                }
            } else {
                logValidationError("Ingredients must be either a JsonObject or a JsonArray!");
                return false;
            }
        }
        return super.validateSpecific();
    }

    public List<JsonElement> getIngredients() {
        return ingredients;
    }
}
