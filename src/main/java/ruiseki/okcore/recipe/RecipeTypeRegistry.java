package ruiseki.okcore.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.event.recipes.RecipesUpdatedEvent;
import ruiseki.okcore.init.ModBase;

public class RecipeTypeRegistry {

    private final ModBase mod;
    private final Map<ResourceLocation, IRecipeType<?>> recipeTypes;

    public RecipeTypeRegistry(ModBase mod) {
        this.mod = mod;
        this.recipeTypes = new HashMap<>();
    }

    public <T extends IRecipeOK<?>> IRecipeType<T> register(final String key) {
        final ResourceLocation id = new ResourceLocation(this.mod.getModId(), key);
        final IRecipeType<T> type = RecipeRegistry.registerType(id);
        this.recipeTypes.put(id, type);
        return type;
    }

    private void onRecipesSynced(RecipesUpdatedEvent event) {
        for (final IRecipeType<?> type : this.recipeTypes.values()) {
            final Map<ResourceLocation, ?> recipes = event.getRecipeManager()
                .getRecipes(type);
            final int namespaces = recipes.keySet()
                .stream()
                .map(ResourceLocation::getResourceDomain)
                .collect(Collectors.toSet())
                .size();
            this.mod.getLoggerHelper()
                .getLogger()
                .info("Loaded {} {} recipes from {} namespaces", recipes.size(), type.toString(), namespaces);
        }
    }
}
