package ruiseki.okcore.recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.DataManager;
import ruiseki.okcore.data.SimpleJsonResourceReloadListener;
import ruiseki.okcore.data.condition.ConditionRegistry;
import ruiseki.okcore.data.condition.ICondition;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.init.IRegistry;

public class RecipeManager extends SimpleJsonResourceReloadListener implements IRegistry {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    private static final RecipeManager INSTANCE = new RecipeManager();

    protected Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> recipes = Collections.emptyMap();
    private Map<ResourceLocation, IRecipeOK<?>> byName = Collections.emptyMap();
    private ICondition.IContext context;

    public RecipeManager() {
        this(ICondition.IContext.EMPTY);
    }

    public RecipeManager(ICondition.IContext context) {
        super(GSON, "recipes");
        this.context = context;
    }

    public static RecipeManager getManager() {
        return INSTANCE;
    }

    @ApiStatus.Internal
    public void setContext(ICondition.IContext context) {
        this.context = context;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, DataManager manager) {
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> map = new Reference2ObjectOpenHashMap<>();
        Map<ResourceLocation, IRecipeOK<?>> builderByName = new Object2ObjectOpenHashMap<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            ResourceLocation id = entry.getKey();
            if (id.getResourcePath()
                .startsWith("_")) continue;

            JsonElement element = entry.getValue();

            try {
                if (element != null && element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    if (!ConditionRegistry.processConditions(jsonObject, context)) {
                        OKCore.okLog(Level.INFO, "Skipping loading recipe {} as its conditions were not met", id);
                        continue;
                    }

                    IRecipeOK<?> recipe = RecipeRegistry.deserialize(id, jsonObject);
                    if (recipe == null) {
                        OKCore.okLog(Level.INFO, "Skipping loading recipe {} as its serializer returned null", id);
                        continue;
                    }

                    map.computeIfAbsent(recipe.getType(), type -> new Object2ObjectOpenHashMap<>())
                        .put(id, recipe);

                    builderByName.put(id, recipe);
                }
            } catch (IllegalArgumentException | JsonParseException jsonparseexception) {
                OKCore.okLog(Level.ERROR, "Parsing error loading recipe {}", id, jsonparseexception);
            }
        }

        this.recipes = Collections.unmodifiableMap(map);
        this.byName = Collections.unmodifiableMap(builderByName);

        OKCore.okLog(Level.INFO, "Loaded {} recipes", this.byName.size());
    }

    @SuppressWarnings("unchecked")
    private <C extends IInventory, T extends IRecipeOK<C>> Map<ResourceLocation, T> byType(IRecipeType<T> type) {
        if (type == null) return Collections.emptyMap();
        return (Map<ResourceLocation, T>) (Map<ResourceLocation, ?>) this.recipes
            .getOrDefault(type, Collections.emptyMap());
    }

    public <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getRecipeFor(IRecipeType<T> type, C inventory,
        World world) {
        return this.byType(type)
            .values()
            .stream()
            .filter(recipe -> recipe.matchesOK(inventory, world))
            .findFirst();
    }

    public <C extends IInventory, T extends IRecipeOK<C>> Optional<Pair<ResourceLocation, T>> getRecipeFor(
        IRecipeType<T> type, C inventory, World world, @Nullable ResourceLocation lastRecipe) {

        Map<ResourceLocation, T> map = this.byType(type);
        if (lastRecipe != null) {
            T t = map.get(lastRecipe);
            if (t != null && t.matchesOK(inventory, world)) {
                return Optional.of(Pair.of(lastRecipe, t));
            }
        }

        return map.entrySet()
            .stream()
            .filter(
                entry -> entry.getValue()
                    .matchesOK(inventory, world))
            .findFirst()
            .map(entry -> Pair.of(entry.getKey(), entry.getValue()));
    }

    public <C extends IInventory, T extends IRecipeOK<C>> List<T> getAllRecipesFor(IRecipeType<T> type) {
        return List.copyOf(
            this.byType(type)
                .values());
    }

    public <C extends IInventory, T extends IRecipeOK<C>> List<T> getRecipesFor(IRecipeType<T> type, C inventory,
        World world) {
        return this.byType(type)
            .values()
            .stream()
            .filter(recipe -> recipe.matchesOK(inventory, world))
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

    public Optional<? extends IRecipeOK<?>> byKey(ResourceLocation location) {
        if (location == null) return Optional.empty();
        return Optional.ofNullable(this.byName.get(location));
    }

    public Collection<IRecipeOK<?>> getRecipes() {
        return this.recipes.values()
            .stream()
            .flatMap(
                map -> map.values()
                    .stream())
            .collect(Collectors.toSet());
    }

    public Stream<ResourceLocation> getRecipeIds() {
        return this.recipes.values()
            .stream()
            .flatMap(
                map -> map.keySet()
                    .stream());
    }

    @SideOnly(Side.CLIENT)
    public void replaceRecipes(Iterable<IRecipeOK<?>> serverRecipes) {
        Map<IRecipeType<?>, Map<ResourceLocation, IRecipeOK<?>>> map = new Reference2ObjectOpenHashMap<>();
        Map<ResourceLocation, IRecipeOK<?>> builder = new Object2ObjectOpenHashMap<>();

        serverRecipes.forEach(iRecipeOK -> {
            Map<ResourceLocation, IRecipeOK<?>> map1 = map
                .computeIfAbsent(iRecipeOK.getType(), key -> new Object2ObjectOpenHashMap<>());
            ResourceLocation resourcelocation = iRecipeOK.getId();
            IRecipeOK<?> recipe = map1.put(resourcelocation, iRecipeOK);
            builder.put(resourcelocation, iRecipeOK);
            if (recipe != null) {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + resourcelocation);
            }
        });

        this.recipes = Collections.unmodifiableMap(map);
        this.byName = Collections.unmodifiableMap(builder);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> RecipeManager.CachedCheck<C, T> createCheck(
        final IRecipeType<T> type) {
        return new RecipeManager.CachedCheck<C, T>() {

            @Nullable
            private ResourceLocation lastRecipe;

            @Override
            public Optional<T> getRecipeFor(C inventory, World world) {
                RecipeManager recipeManager = RecipeManager.getManager();
                Optional<Pair<ResourceLocation, T>> optional = recipeManager
                    .getRecipeFor(type, inventory, world, this.lastRecipe);
                if (optional.isPresent()) {
                    Pair<ResourceLocation, T> pair = optional.get();
                    this.lastRecipe = pair.getLeft();
                    return Optional.of(pair.getRight());
                } else {
                    return Optional.empty();
                }
            }
        };
    }

    public interface CachedCheck<C extends IInventory, T extends IRecipeOK<C>> {

        Optional<T> getRecipeFor(C inventory, World world);
    }
}
