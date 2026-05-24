package ruiseki.okcore.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
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
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingRecipe;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

public class RecipeRegistry {

    private static final Map<String, IRecipeSerializer<?>> SERIALIZER_MAPPING = new HashMap<>();
    private static final Map<String, IRecipeType<?>> TYPE_MAPPING = new HashMap<>();
    private static final Map<ResourceLocation, RecipeHolder> RECIPE_HOLDERS = new HashMap<>();

    private static final Map<ItemStack, SmeltingRecipe> FURNACE_BRIDGE_MAP = new HashMap<>();

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
            } catch (Exception e) {
                OKCore.okLog(Level.ERROR, "Failed to register RecipeSorter for type [{}]: {}", key, e.toString());
            }
        }
        return recipeType;
    }

    public static <T extends IRecipeOK<?>> IRecipeSerializer<T> registerSerializer(
        IRecipeSerializer<T> recipeSerializer) {
        if (recipeSerializer == null || recipeSerializer.getTypeKey() == null) return null;
        SERIALIZER_MAPPING.put(recipeSerializer.getTypeKey(), recipeSerializer);
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

        syncMCCraftingManager();
        syncMCFurnaceRecipes();

        GameRegistry.registerFuelHandler(new IFuelHandler() {

            @Override
            public int getBurnTime(ItemStack fuel) {
                if (fuel == null || fuel.getItem() == null) return 0;

                Collection<FuelRecipe> fuelRecipes = CraftingHelpers.getFuelRecipes();
                if (fuelRecipes == null || fuelRecipes.isEmpty()) return 0;

                for (FuelRecipe fuelRecipe : fuelRecipes) {
                    if (fuelRecipe != null && fuelRecipe.matchesFuel(fuel)) {
                        return fuelRecipe.getBurnTime();
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

        syncMCCraftingManager();
        syncMCFurnaceRecipes();

        OKCore.okLog(Level.INFO, "Injected {} custom world recipes into current session.", allWorldRecipes.size());
        RECIPE_HOLDERS.clear();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void syncMCCraftingManager() {
        Collection<IRecipeOK<?>> targetRecipes = RecipeManager.getManager()
            .getRecipes();
        List mcRecipeList = CraftingManager.getInstance()
            .getRecipeList();
        if (mcRecipeList == null) return;

        mcRecipeList.removeIf(obj -> obj instanceof IRecipeOK);

        if (targetRecipes == null || targetRecipes.isEmpty()) return;
        for (IRecipeOK<?> recipe : targetRecipes) {
            if (recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe) {
                mcRecipeList.add(recipe);
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void syncMCFurnaceRecipes() {
        Collection<IRecipeOK<?>> targetRecipes = RecipeManager.getManager()
            .getRecipes();
        FurnaceRecipes furnaceInstance = FurnaceRecipes.smelting();
        Map mcSmeltingList = furnaceInstance.getSmeltingList();
        Map mcExperienceList = furnaceInstance.experienceList;

        if (mcSmeltingList == null) return;
        for (ItemStack registeredOutput : FURNACE_BRIDGE_MAP.keySet()) {
            Iterator<Map.Entry> smeltingIterator = mcSmeltingList.entrySet()
                .iterator();
            while (smeltingIterator.hasNext()) {
                Map.Entry entry = smeltingIterator.next();
                if (entry.getValue() == registeredOutput) {
                    smeltingIterator.remove();
                }
            }

            if (mcExperienceList != null) {
                mcExperienceList.remove(registeredOutput);
            }
        }

        FURNACE_BRIDGE_MAP.clear();

        if (targetRecipes == null || targetRecipes.isEmpty()) return;

        for (IRecipeOK<?> recipe : targetRecipes) {
            if (recipe instanceof SmeltingRecipe customRecipe) {
                ItemStack customOutput = customRecipe.getRecipeOutput();
                float customExp = customRecipe.getExperience();

                if (customRecipe.getIngredient() == null || customOutput == null) continue;

                List<ItemStack> matchingStacks = customRecipe.getIngredient()
                    .toStacks();
                if (matchingStacks == null || matchingStacks.isEmpty()) continue;

                ItemStack representInput = matchingStacks.getFirst();
                if (representInput == null) continue;

                mcSmeltingList.put(representInput, customOutput);
                if (mcExperienceList != null) {
                    mcExperienceList.put(customOutput, customExp);
                }
                FURNACE_BRIDGE_MAP.put(customOutput, customRecipe);
            }
        }
    }
}
