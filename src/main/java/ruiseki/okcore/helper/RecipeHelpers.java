package ruiseki.okcore.helper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.data.loader.recipes.IRecipeType;
import ruiseki.okcore.data.loader.recipes.RecipeHandler;
import ruiseki.okcore.recipe.RecipeDataBase;

public class RecipeHelpers {

    @SuppressWarnings("unchecked")
    public static <T extends RecipeDataBase> Map<ResourceLocation, T> getRecipes(IRecipeType<?> recipeType) {
        if (recipeType == null || recipeType.getTypeKey() == null) {
            return Collections.emptyMap();
        }

        Map<ResourceLocation, T> recipeMap = new HashMap<>();
        List<IRecipe> allRecipes = CraftingManager.getInstance()
            .getRecipeList();

        for (IRecipe recipe : allRecipes) {
            if (recipe instanceof RecipeDataBase coreRecipe) {
                if (coreRecipe.getRecipeType() != null) {
                    String targetKey = recipeType.getTypeKey();
                    String recipeKey = coreRecipe.getRecipeType()
                        .getTypeKey();
                    if (targetKey.equals(recipeKey)) {
                        if (coreRecipe.getId() != null) {
                            recipeMap.put(coreRecipe.getId(), (T) coreRecipe);
                        }
                    }
                }
            }
        }
        return recipeMap;
    }

    public static <T extends RecipeDataBase> Map<ResourceLocation, T> getRecipes(String typeKey) {
        IRecipeType<?> recipeType = RecipeHandler.getType(typeKey);
        if (recipeType == null) {
            return Collections.emptyMap();
        }
        return getRecipes(recipeType);
    }

    public static <T extends RecipeDataBase> T getRecipeById(ResourceLocation id, IRecipeType<?> recipeType) {
        if (id == null || recipeType == null) return null;
        return RecipeHelpers.<T>getRecipes(recipeType)
            .get(id);
    }
}
