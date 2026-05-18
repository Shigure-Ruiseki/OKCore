package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.data.loader.recipes.IRecipeType;
import ruiseki.okcore.data.loader.recipes.RecipeHandler;
import ruiseki.okcore.datastructure.ThreadsafeCache;
import ruiseki.okcore.recipe.RecipeDataBase;

public class RecipeHelpers {

    private static final ThreadsafeCache<String, List<RecipeDataBase>> RECIPE_CACHE = new ThreadsafeCache<>(
        1024,
        key -> buildRecipeListForType((String) key),
        false);

    public static void invalidate() {
        RECIPE_CACHE.clear();
    }

    private static List<RecipeDataBase> buildRecipeListForType(String typeKey) {
        List<RecipeDataBase> localList = new ArrayList<>();
        List<IRecipe> allRecipes = CraftingManager.getInstance()
            .getRecipeList();

        for (IRecipe recipe : allRecipes) {
            if (recipe instanceof RecipeDataBase coreRecipe) {
                if (coreRecipe.getRecipeType() != null && typeKey.equals(
                    coreRecipe.getRecipeType()
                        .getTypeKey())) {
                    localList.add(coreRecipe);
                }
            }
        }
        return localList.isEmpty() ? Collections.emptyList() : Collections.unmodifiableList(localList);
    }

    @SuppressWarnings("unchecked")
    public static <T extends RecipeDataBase> List<T> getRecipeList(IRecipeType<?> recipeType) {
        if (recipeType == null || recipeType.getTypeKey() == null) {
            return Collections.emptyList();
        }
        return (List<T>) RECIPE_CACHE.get(recipeType.getTypeKey());
    }

    public static <T extends RecipeDataBase> List<T> getRecipeList(String typeKey) {
        IRecipeType<?> recipeType = RecipeHandler.getType(typeKey);
        if (recipeType == null) {
            return Collections.emptyList();
        }
        return getRecipeList(recipeType);
    }

    public static <T extends RecipeDataBase> T getRecipeById(ResourceLocation id, IRecipeType<?> recipeType) {
        if (id == null || recipeType == null) return null;
        List<T> recipes = getRecipeList(recipeType);
        for (T recipe : recipes) {
            if (id.equals(recipe.getId())) {
                return recipe;
            }
        }
        return null;
    }
}
