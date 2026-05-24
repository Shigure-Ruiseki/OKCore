package ruiseki.okcore.recipe;

import static ruiseki.okcore.recipe.type.cooking.furnace.SmeltingType.SMELTING;
import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;
import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

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
import ruiseki.okcore.helper.NEIHelpers;
import ruiseki.okcore.lib.LibMods;
import ruiseki.okcore.recipe.type.cooking.fuel.FuelRecipe;
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingRecipe;

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

        syncMCCraftingManager(allBaseRecipes);
        syncMCFurnaceRecipes(allBaseRecipes);

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

        syncMCCraftingManager(allWorldRecipes);
        syncMCFurnaceRecipes(allWorldRecipes);

        if (LibMods.NotEnoughItems.isModLoaded()) {
            NEIHelpers.reloadNEIFuels();
        }

        OKCore.okLog(Level.INFO, "Injected {} custom world recipes into current session.", allWorldRecipes.size());
        RECIPE_HOLDERS.clear();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void syncMCCraftingManager(List<IRecipeOK<?>> targetRecipes) {
        if (targetRecipes == null || targetRecipes.isEmpty()) return;

        List mcRecipeList = CraftingManager.getInstance()
            .getRecipeList();
        if (mcRecipeList == null || mcRecipeList.isEmpty()) return;

        IRecipeType<?> shapedType = RecipeRegistry.getType(SHAPED);
        IRecipeType<?> shapelessType = RecipeRegistry.getType(SHAPELESS);

        Map<ResourceLocation, IRecipeOK<?>> incomingMap = new HashMap<>(targetRecipes.size());
        for (IRecipeOK<?> recipe : targetRecipes) {
            if (recipe != null) {
                IRecipeType<?> type = recipe.getType();
                if (type == shapedType || type == shapelessType) {
                    incomingMap.put(recipe.getId(), recipe);
                }
            }
        }

        if (incomingMap.isEmpty()) return;

        List toRemove = new ArrayList<>(Math.min(32, incomingMap.size()));
        for (Object obj : mcRecipeList) {
            if (obj instanceof IRecipeOK<?>mcRecipe) {
                ResourceLocation mcId = mcRecipe.getId();
                if (mcId != null && incomingMap.containsKey(mcId)) {
                    IRecipeOK<?> incomingRecipe = incomingMap.get(mcId);
                    if (mcRecipe.equals(incomingRecipe)) {
                        incomingMap.remove(mcId);
                    } else {
                        toRemove.add(obj);
                    }
                }
            }
        }

        if (!toRemove.isEmpty()) mcRecipeList.removeAll(toRemove);
        if (!incomingMap.isEmpty()) mcRecipeList.addAll(incomingMap.values());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void syncMCFurnaceRecipes(List<IRecipeOK<?>> targetRecipes) {
        if (targetRecipes == null || targetRecipes.isEmpty()) return;

        FurnaceRecipes furnaceInstance = FurnaceRecipes.smelting();
        Map mcSmeltingList = furnaceInstance.getSmeltingList();

        Map mcExperienceList = furnaceInstance.experienceList;

        if (mcSmeltingList == null) return;

        IRecipeType<?> smeltingType = RecipeRegistry.getType(SMELTING);

        Map<ResourceLocation, SmeltingRecipe> incomingMap = new HashMap<>();
        for (IRecipeOK<?> recipe : targetRecipes) {
            if (recipe instanceof SmeltingRecipe smeltingRecipe && recipe.getType() == smeltingType) {
                incomingMap.put(recipe.getId(), smeltingRecipe);
            }
        }

        if (incomingMap.isEmpty()) return;

        Iterator<Map.Entry> iterator = mcSmeltingList.entrySet()
            .iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            ItemStack mcOutputVal = (ItemStack) entry.getValue();
            SmeltingRecipe mcRecipe = FURNACE_BRIDGE_MAP.get(mcOutputVal);

            if (mcRecipe != null) {
                ResourceLocation mcId = mcRecipe.getId();
                if (mcId != null && incomingMap.containsKey(mcId)) {
                    SmeltingRecipe incomingRecipe = incomingMap.get(mcId);
                    if (mcRecipe.equals(incomingRecipe)) {
                        incomingMap.remove(mcId);
                    } else {
                        iterator.remove();
                        if (mcExperienceList != null) {
                            mcExperienceList.remove(mcOutputVal);
                        }
                        FURNACE_BRIDGE_MAP.remove(mcOutputVal);
                    }
                }
            }
        }

        if (!incomingMap.isEmpty()) {
            for (SmeltingRecipe customRecipe : incomingMap.values()) {
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
