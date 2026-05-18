package ruiseki.okcore.data.loader.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.json.item.ItemMaterial;

@RecipeData
public class ShapelessRecipeMaterial extends AbstractRecipeMaterial<ShapelessOreRecipe>
    implements IRecipeType<IRecipe> {

    private List<JsonElement> ingredients;

    @Override
    public String getTypeKey() {
        return "minecraft:crafting_shapeless";
    }

    @Override
    public void fromJson(ResourceLocation id, JsonObject json) {
        super.fromJson(id, json);
        if (json.has("result")) {
            JsonElement resultElement = json.get("result");
            if (resultElement.isJsonObject()) {
                JsonObject resultObj = resultElement.getAsJsonObject();
                this.result = new ItemMaterial();
                this.result.read(resultObj);
            }
        }
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
    }

    @Override
    public List<ShapelessOreRecipe> getRecipes() {
        List<ShapelessOreRecipe> recipeList = new ArrayList<>();
        ItemStack outputStack = this.result != null ? this.result.toStack() : null;
        if (outputStack == null) {
            OKCore.okLog(
                Level.ERROR,
                "Shapeless Recipe {}: Cannot generate recipes because the output result 'ItemStack' resolves to NULL!",
                id);
            return recipeList;
        }

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
                OKCore.okLog(
                    Level.ERROR,
                    "Shapeless Recipe {}: Ingredient at index failed to resolve into any active Item or OreDictionary tag! Recipe generation aborted.",
                    id);
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
            } catch (Throwable t) {
                OKCore.okLog(
                    Level.ERROR,
                    "Shapeless Recipe {}: Forge layout constructor rejected this shapeless combination! Internal Forge error: {}",
                    id,
                    t.toString());
            }
        }

        if (recipeList.isEmpty()) {
            OKCore.okLog(
                org.apache.logging.log4j.Level.WARN,
                "Shapeless Recipe {}: Compiled successfully but 0 valid active recipes could be generated.",
                id);
        }

        return recipeList;
    }

    private void resolveIngredient(JsonObject jsonObject, List<Object> targetList) {
        ItemMaterial mat = new ItemMaterial();
        mat.read(jsonObject);

        if (!mat.validate()) return;
        if (mat.getOre() != null) {
            String targetOre = mat.getOre()
                .trim();
            if (!OreDictionary.getOres(targetOre)
                .isEmpty()) {
                targetList.add(targetOre);
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
    public boolean validate() {
        if (this.ingredients == null || this.ingredients.isEmpty()) {
            OKCore.okLog(Level.ERROR, "Recipe [{}] ingredients list is missing or empty!", id);
            return false;
        }

        if (this.ingredients.size() > 9) {
            OKCore.okLog(Level.ERROR, "Recipe [{}] cannot have more than 9 ingredients!", id);
            return false;
        }

        for (JsonElement element : this.ingredients) {
            if (element.isJsonObject()) {
                ItemMaterial mat = new ItemMaterial();
                mat.read(element.getAsJsonObject());
                if (!mat.validate()) {
                    OKCore.okLog(Level.ERROR, "Recipe [{}] invalid ItemMaterial structure inside ingredients", id);
                    return false;
                }
            } else if (element.isJsonArray()) {
                for (JsonElement subElement : element.getAsJsonArray()) {
                    if (!subElement.isJsonObject()) {
                        OKCore.okLog(
                            Level.ERROR,
                            "Recipe [{}] array elements inside ingredients must be JsonObjects!",
                            id);
                        return false;
                    }
                    ItemMaterial mat = new ItemMaterial();
                    mat.read(subElement.getAsJsonObject());
                    if (!mat.validate()) {
                        OKCore.okLog(
                            Level.ERROR,
                            "Recipe [{}] invalid ItemMaterial structure inside Array of ingredients",
                            id);
                        return false;
                    }
                }
            } else {
                OKCore.okLog(Level.ERROR, "Recipe [{}] ingredients must be either a JsonObject or a JsonArray!", id);
                return false;
            }
        }
        return super.validate();
    }

    public List<JsonElement> getIngredients() {
        return ingredients;
    }
}
