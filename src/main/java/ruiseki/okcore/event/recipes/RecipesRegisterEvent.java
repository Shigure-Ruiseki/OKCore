package ruiseki.okcore.event.recipes;

import java.util.Map;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.eventhandler.Event;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;

public class RecipesRegisterEvent extends Event {

    private final RecipeManager recipeManager;
    private final Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> recipesByType;
    private final Map<ResourceLocation, IRecipeOK<?>> recipesByName;

    public RecipesRegisterEvent(RecipeManager recipeManager,
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> recipesByType,
        Map<ResourceLocation, IRecipeOK<?>> recipesByName) {
        this.recipeManager = recipeManager;
        this.recipesByType = recipesByType;
        this.recipesByName = recipesByName;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> getRecipesByType() {
        return recipesByType;
    }

    public Map<ResourceLocation, IRecipeOK<?>> getRecipesByName() {
        return recipesByName;
    }

    public void addRecipe(IRecipeOK<?> recipe) {
        if (recipe != null && recipe.getId() != null) {
            this.recipesByType.computeIfAbsent(recipe.getType(), k -> new Object2ObjectOpenHashMap<>())
                .put(recipe.getId(), recipe);
            this.recipesByName.put(recipe.getId(), recipe);
        }
    }

    public void removeRecipe(ResourceLocation id) {
        IRecipeOK<?> removed = this.recipesByName.remove(id);
        if (removed != null && this.recipesByType.containsKey(removed.getType())) {
            this.recipesByType.get(removed.getType())
                .remove(id);
        }
    }
}
