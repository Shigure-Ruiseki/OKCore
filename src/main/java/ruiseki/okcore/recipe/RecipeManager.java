package ruiseki.okcore.recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.Helpers;

public class RecipeManager {

    private static RecipeManager instance;

    private static Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> globalBaseRecipes = ImmutableMap.of();
    private Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> recipes = ImmutableMap.of();

    public RecipeManager() {}

    public static RecipeManager getManager() {
        if (instance == null) {
            instance = new RecipeManager();
            instance.loadFromBase();
        }
        return instance;
    }

    public static void validateManager() {
        instance = new RecipeManager();
        instance.loadFromBase();
    }

    public static void invalidateManager() {
        instance = null;
    }

    @SuppressWarnings("unchecked")
    public <R extends IRecipeOK<?>> Collection<R> getRecipesByType(IRecipeType<R> type) {
        if (type == null) return Collections.emptyList();
        Map<ResourceLocation, ?> rawMap = this.recipes.get(type);
        if (rawMap == null || rawMap.isEmpty()) return Collections.emptyList();
        return ((Map<ResourceLocation, R>) rawMap).values();
    }

    public Optional<? extends IRecipeOK<?>> getRecipesByKey(ResourceLocation location) {
        if (location == null) return Optional.empty();
        return this.recipes.values()
            .stream()
            .map(typeMap -> typeMap.get(location))
            .filter(Objects::nonNull)
            .findFirst();
    }

    public <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getRecipeFor(IRecipeType<T> type, C inventory,
        World world) {
        return this.getRecipesByType(type)
            .stream()
            .map(recipe -> (T) recipe)
            .flatMap(recipeOK -> Helpers.toStream(type.tryMatch(recipeOK, world, inventory)))
            .findFirst();
    }

    public <C extends IInventory, T extends IRecipeOK<C>> Collection<T> getRecipesFor(IRecipeType<T> type, C inventory,
        World world) {
        return this.getRecipesByType(type)
            .stream()
            .map(recipe -> (T) recipe)
            .flatMap(recipeOK -> Helpers.toStream(type.tryMatch(recipeOK, world, inventory)))
            .sorted(Comparator.comparing(recipe -> {
                ItemStack output = recipe.getRecipeOutput();
                if (output == null || output.getItem() == null) return "";
                return output.getItem()
                    .getUnlocalizedName(output);
            }))
            .collect(Collectors.toList());
    }

    public <C extends IInventory, T extends IRecipeOK<C>> NonNullList<ItemStack> getRemainingItemsFor(
        IRecipeType<T> type, C inventory, World world) {

        return this.getRecipeFor(type, inventory, world)
            .map(recipe -> recipe.getRemainingItems(inventory))
            .orElseGet(() -> {
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventory.getSizeInventory(), null);
                for (int i = 0; i < nonnulllist.size(); ++i) {
                    nonnulllist.set(i, inventory.getStackInSlot(i));
                }
                return nonnulllist;
            });
    }

    public Collection<IRecipeOK<?>> getRecipes() {
        return this.recipes.values()
            .stream()
            .flatMap(
                typeMap -> typeMap.values()
                    .stream())
            .collect(Collectors.toSet());
    }

    public Stream<ResourceLocation> getRecipeIds() {
        return this.recipes.values()
            .stream()
            .flatMap(
                typeMap -> typeMap.keySet()
                    .stream());
    }

    @SideOnly(Side.CLIENT)
    public void replaceRecipes(Iterable<IRecipeOK<?>> serverRecipes) {
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> map = Maps.newHashMap();

        serverRecipes.forEach((recipe) -> {
            Map<ResourceLocation, IRecipeOK<?>> typeMap = map.computeIfAbsent(recipe.getType(), k -> Maps.newHashMap());
            IRecipeOK<?> oldRecipe = typeMap.put(recipe.getId(), recipe);
            if (oldRecipe != null) {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
            }
        });

        this.recipes = copyToImmutable(map);
    }

    private static Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> copyToImmutable(
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> map) {

        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> copy = new HashMap<>();
        for (Map.Entry<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> entry : map.entrySet()) {
            copy.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
        }
        return ImmutableMap.copyOf(copy);
    }

    public static void setupGlobalRecipes(Iterable<IRecipeOK<?>> baseRecipes) {
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> mutableMap = new HashMap<>();

        if (baseRecipes != null) {
            for (IRecipeOK<?> recipe : baseRecipes) {
                mutableMap.computeIfAbsent(recipe.getType(), k -> new HashMap<>())
                    .put(recipe.getId(), recipe);
            }
        }

        globalBaseRecipes = copyToImmutable(mutableMap);
    }

    public void addWorldRecipes(Iterable<IRecipeOK<?>> worldRecipes) {
        if (worldRecipes == null) return;

        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> mutableMap = new HashMap<>();
        for (Map.Entry<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> entry : this.recipes.entrySet()) {
            mutableMap.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }

        for (IRecipeOK<?> recipe : worldRecipes) {
            ResourceLocation id = recipe.getId();
            for (Map<ResourceLocation, IRecipeOK<?>> typeMap : mutableMap.values()) {
                typeMap.remove(id);
            }
            mutableMap.computeIfAbsent(recipe.getType(), k -> new HashMap<>())
                .put(id, recipe);
        }

        this.recipes = copyToImmutable(mutableMap);
    }

    public void loadFromBase() {
        this.recipes = cloneMap(globalBaseRecipes);
    }

    @SuppressWarnings("unchecked")
    private Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> cloneMap(
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> original) {
        if (original == null) return Maps.newHashMap();

        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> copy = Maps.newHashMap();

        for (Map.Entry<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> entry : original.entrySet()) {
            Map<ResourceLocation, IRecipeOK<?>> typeMapCopy = Maps.newHashMap(entry.getValue());
            copy.put(entry.getKey(), typeMapCopy);
        }

        return copy;
    }
}
