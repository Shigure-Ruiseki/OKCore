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

import org.apache.logging.log4j.Level;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataManager;
import ruiseki.okcore.data.SimpleJsonResourceReloadListener;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.Helpers;

public class RecipeManager extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    private static RecipeManager instance;

    protected Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> recipes = ImmutableMap.of();
    private Map<ResourceLocation, IRecipeOK<?>> byName = ImmutableMap.of();

    public RecipeManager() {
        super(GSON, "recipes");
    }

    public static RecipeManager getManager() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, DataManager manager) {
        Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipeOK<?>>> map = new HashMap<>();
        ImmutableMap.Builder<ResourceLocation, IRecipeOK<?>> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            ResourceLocation id = entry.getKey();
            JsonElement element = entry.getValue();

            try {
                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    IRecipeOK<?> recipe = RecipeRegistry.deserialize(id, jsonObject);
                    if (recipe == null) {
                        OKCore.okLog(Level.INFO, "Skipping loading recipe {} as its serializer returned null", id);
                        continue;
                    }

                    map.computeIfAbsent(recipe.getType(), (type) -> { return ImmutableMap.builder(); })
                        .put(id, recipe);

                    builder.put(id, recipe);
                }
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                OKCore.okLog(Level.ERROR, "Parsing error loading recipe {}", id, jsonparseexception);
            }
        }

        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> builtMap = map.entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue()
                        .build()));

        this.recipes = ImmutableMap.copyOf(builtMap);

        this.byName = builder.build();

        RecipeRegistry.syncMCCraftingManager();
        RecipeRegistry.syncMCFurnaceRecipes();

        OKCore.okLog(Level.INFO, "Loaded {} recipes", this.byName.size());
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

    @SuppressWarnings("unchecked")
    public <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getRecipeFor(IRecipeType<T> type, C inventory,
        World world) {
        return this.getRecipesByType(type)
            .stream()
            .map(recipe -> (T) recipe)
            .flatMap(recipeOK -> Helpers.toStream(type.tryMatch(recipeOK, world, inventory)))
            .findFirst();
    }

    @SuppressWarnings("unchecked")
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

    public Optional<? extends IRecipeOK<?>> byKey(ResourceLocation p_44044_) {
        return Optional.ofNullable(this.byName.get(p_44044_));
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
        ImmutableMap.Builder<ResourceLocation, IRecipeOK<?>> builder = ImmutableMap.builder();
        serverRecipes.forEach((iRecipeOK) -> {
            Map<ResourceLocation, IRecipeOK<?>> map1 = map
                .computeIfAbsent(iRecipeOK.getType(), (p_220272_) -> { return Maps.newHashMap(); });
            ResourceLocation resourcelocation = iRecipeOK.getId();
            IRecipeOK<?> recipe = map1.put(resourcelocation, iRecipeOK);
            builder.put(resourcelocation, iRecipeOK);
            if (recipe != null) {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + resourcelocation);
            }
        });
        this.recipes = ImmutableMap.copyOf(map);
        this.byName = builder.build();
    }
}
