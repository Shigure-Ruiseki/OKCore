package ruiseki.okcore.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.RecipeSorter;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.data.loader.recipes.RecipeHolder;
import ruiseki.okcore.helper.CraftingHelpers;
import ruiseki.okcore.recipe.type.cooking.fuel.FuelRecipe;

public class RecipeRegistry {

    private static final Map<String, IRecipeSerializer<?>> SERIALIZER_MAPPING = new HashMap<>();
    private static final Map<String, IRecipeType<?>> TYPE_MAPPING = new HashMap<>();
    private static final Map<ResourceLocation, RecipeHolder> RECIPE_HOLDERS = new HashMap<>();

    public static <T extends IRecipeOK<?>> IRecipeType<T> registerType(IRecipeType<T> recipeType) {
        if (recipeType == null || recipeType.getTypeKey() == null) return null;
        String key = recipeType.getTypeKey();
        TYPE_MAPPING.put(key, recipeType);

        if (recipeType.isForgeRecipe() && recipeType.getRecipeClass() != null) {
            try {
                RecipeSorter.register(
                    recipeType.getTypeKey(),
                    recipeType.getRecipeClass(),
                    recipeType.getSorterCategory(),
                    recipeType.getSorterDependencies());
                OKCore.okLog(Level.INFO, "Registered RecipeSorter for Forge recipe type: [{}]", key);
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Failed to register RecipeSorter for type [{}]: {}", key, e.toString());
            }
        }

        return recipeType;
    }

    public static <T extends IRecipeOK<?>> IRecipeSerializer<T> registerSerializer(
        IRecipeSerializer<T> recipeSerializer) {
        if (recipeSerializer == null || recipeSerializer.getTypeKey() == null) return null;
        String key = recipeSerializer.getTypeKey();
        SERIALIZER_MAPPING.put(key, recipeSerializer);
        return recipeSerializer;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IRecipeOK<?>> IRecipeType<T> getType(String key) {
        if (key == null) return null;
        return (IRecipeType<T>) TYPE_MAPPING.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IRecipeOK<?>> IRecipeSerializer<T> getSerializer(String type) {
        if (type == null) return null;
        return (IRecipeSerializer<T>) SERIALIZER_MAPPING.get(type);
    }

    public static void loadFromASM(ASMDataTable dataTable) {
        for (ASMDataTable.ASMData data : dataTable.getAll(RecipeData.class.getCanonicalName())) {
            try {
                Class<?> clazz = Class.forName(data.getClassName());

                boolean isType = IRecipeType.class.isAssignableFrom(clazz);
                boolean isSerializer = IRecipeSerializer.class.isAssignableFrom(clazz);

                if (!isType && !isSerializer) continue;

                Object instance = clazz.getDeclaredConstructor()
                    .newInstance();
                boolean registeredAny = false;

                if (isType) {
                    IRecipeType<?> recipeType = (IRecipeType<?>) instance;
                    if (recipeType.shouldRegisterType()) {
                        registerType(recipeType);
                        registeredAny = true;
                    } else {
                        OKCore
                            .okLog(Level.INFO, "Skipping recipe type: {} (Condition not met)", recipeType.getTypeKey());
                    }
                }

                if (isSerializer) {
                    IRecipeSerializer<?> recipeSerializer = (IRecipeSerializer<?>) instance;
                    if (recipeSerializer.shouldRegisterSerializer()) {
                        registerSerializer(recipeSerializer);
                        registeredAny = true;
                    } else {
                        OKCore.okLog(
                            Level.INFO,
                            "Skipping recipe serializer: {} (Condition not met)",
                            recipeSerializer.getTypeKey());
                    }
                }

                if (registeredAny) {
                    OKCore.okLog(
                        Level.INFO,
                        "Successfully registered @RecipeData components for class: [{}]",
                        data.getClassName());
                }
            } catch (Exception e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to initialize ASM RecipeHandler [{}]: {}",
                    data.getClassName(),
                    e.toString());
            }
        }
    }

    public static void addHolder(RecipeHolder holder) {
        RECIPE_HOLDERS.put(holder.id(), holder);
    }

    @SuppressWarnings("unchecked")
    public static IRecipeOK<?> fromHolder(RecipeHolder holder) {
        if (holder == null) {
            OKCore.okLog(Level.ERROR, "Cannot parse recipe from a null RecipeHolder!");
            return null;
        }

        IRecipeSerializer<?> serializer = RecipeRegistry.getSerializer(holder.type());
        if (serializer == null) {
            OKCore.okLog(
                Level.WARN,
                "Missing recipe serializer for type: '{}' (Recipe ID: '{}')",
                holder.type(),
                holder.id());
            return null;
        }

        try {
            return serializer.fromJson(holder.id(), holder.json());
        } catch (Exception e) {
            OKCore.okLog(
                Level.ERROR,
                "Failed to parse recipe '{}' of type '{}' due to an unexpected error!",
                holder.id(),
                holder.type(),
                e);
            return null;
        }
    }

    public static void processGlobalHolders() {
        List<IRecipeOK<?>> allBaseRecipes = new ArrayList<>();

        for (RecipeHolder holder : RECIPE_HOLDERS.values()) {
            IRecipeOK<?> recipe = fromHolder(holder);
            if (recipe != null) {
                allBaseRecipes.add(recipe);
            }
        }

        RecipeManager.setupGlobalRecipes(allBaseRecipes);

        GameRegistry.registerFuelHandler(new IFuelHandler() {

            @Override
            public int getBurnTime(ItemStack fuel) {
                if (fuel == null || fuel.getItem() == null) {
                    return 0;
                }

                for (IRecipeOK<?> recipe : CraftingHelpers.getFuelRecipes()) {
                    if (recipe instanceof FuelRecipe fuelRecipe) {
                        if (fuelRecipe.matchesFuel(fuel)) {
                            return fuelRecipe.getBurnTime();
                        }
                    }
                }

                return 0;
            }
        });

        OKCore.okLog(Level.INFO, "Registered {} base recipes directly into Global storage.", allBaseRecipes.size());
        RECIPE_HOLDERS.clear();
    }

    public static void processWorldHolders() {
        List<IRecipeOK<?>> allWorldRecipes = new ArrayList<>();

        for (RecipeHolder holder : RECIPE_HOLDERS.values()) {
            IRecipeOK<?> recipe = fromHolder(holder);
            if (recipe != null) {
                allWorldRecipes.add(recipe);
            }
        }

        RecipeManager.getManager()
            .addWorldRecipes(allWorldRecipes);

        OKCore.okLog(Level.INFO, "Injected {} custom world recipes into current session.", allWorldRecipes.size());
        RECIPE_HOLDERS.clear();
    }
}
