package ruiseki.okcore.recipe;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;
import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

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

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<ShapedRecipe> getShapedRecipes() {
        IRecipeType<?> type = RecipeRegistry.getType(SHAPED);
        if (type == null) return Collections.emptyList();
        Map<ResourceLocation, IRecipeOK<?>> map = this.recipes.get(type);
        if (map == null || map.isEmpty()) return Collections.emptyList();
        return (Collection) map.values();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<ShapelessRecipe> getShapelessRecipes() {
        IRecipeType<?> type = RecipeRegistry.getType(SHAPELESS);
        if (type == null) return Collections.emptyList();
        Map<ResourceLocation, IRecipeOK<?>> map = this.recipes.get(type);
        if (map == null || map.isEmpty()) return Collections.emptyList();
        return (Collection) map.values();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Collection<IRecipeOK<?>> getRecipesByType(IRecipeType<?> type) {
        if (type == null) return Collections.emptyList();
        Map<ResourceLocation, IRecipeOK<?>> map = this.recipes.get(type);
        if (map == null || map.isEmpty()) return Collections.emptyList();
        return map.values();
    }

    public <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getRecipeFor(IRecipeType<T> type, C inventory,
        World world) {
        return this.byType(type)
            .values()
            .stream()
            .flatMap((recipeOK) -> { return Helpers.toStream(type.tryMatch(recipeOK, world, inventory)); })
            .findFirst();
    }

    @SuppressWarnings("unchecked")
    public <C extends IInventory, T extends IRecipeOK<C>> List<T> getAllRecipesFor(IRecipeType<T> type) {
        return this.byType(type)
            .values()
            .stream()
            .map((recipeOK) -> { return (T) recipeOK; })
            .collect(Collectors.toList());
    }

    public <C extends IInventory, T extends IRecipeOK<C>> List<T> getRecipesFor(IRecipeType<T> type, C inventory,
        World world) {
        return this.byType(type)
            .values()
            .stream()
            .flatMap((p_215380_3_) -> { return Helpers.toStream(type.tryMatch(p_215380_3_, world, inventory)); })
            .sorted(Comparator.comparing((recipe) -> {
                ItemStack output = recipe.getRecipeOutput();
                if (output == null || output.getItem() == null) return "";
                return output.getItem()
                    .getUnlocalizedName(output);
            }))
            .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <C extends IInventory, T extends IRecipeOK<C>> Map<ResourceLocation, IRecipeOK<C>> byType(
        IRecipeType<T> type) {
        return (Map) this.recipes.getOrDefault(type, Collections.emptyMap());
    }

    public <C extends IInventory, T extends IRecipeOK<C>> NonNullList<ItemStack> getRemainingItemsFor(
        IRecipeType<T> type, C inventory, World world) {
        Optional<T> optional = this.getRecipeFor(type, inventory, world);
        if (optional.isPresent()) {
            return optional.get()
                .getRemainingItems(inventory);
        } else {
            NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventory.getSizeInventory(), null);

            for (int i = 0; i < nonnulllist.size(); ++i) {
                nonnulllist.set(i, inventory.getStackInSlot(i));
            }

            return nonnulllist;
        }
    }

    public Optional<? extends IRecipeOK<?>> byKey(ResourceLocation location) {
        return this.recipes.values()
            .stream()
            .map((p_215368_1_) -> { return p_215368_1_.get(location); })
            .filter(Objects::nonNull)
            .findFirst();
    }

    public Collection<IRecipeOK<?>> getRecipes() {
        return this.recipes.values()
            .stream()
            .flatMap(
                (p_215376_0_) -> {
                    return p_215376_0_.values()
                        .stream();
                })
            .collect(Collectors.toSet());
    }

    public Stream<ResourceLocation> getRecipeIds() {
        return this.recipes.values()
            .stream()
            .flatMap(
                (p_215375_0_) -> {
                    return p_215375_0_.keySet()
                        .stream();
                });
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
