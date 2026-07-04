package ruiseki.okcore.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.NEIHelpers;
import ruiseki.okcore.lib.LibMods;
import ruiseki.okcore.recipe.type.cooking.fuel.FuelRecipe;
import ruiseki.okcore.recipe.type.cooking.fuel.FuelSerializer;
import ruiseki.okcore.recipe.type.cooking.fuel.FuelType;
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingRecipe;
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingSerializer;
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingType;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeSerializer;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeSerializer;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType;
import ruiseki.okcore.recipe.type.other.NoneRecipe;
import ruiseki.okcore.recipe.type.other.NoneRecipeSerializer;
import ruiseki.okcore.recipe.type.other.NoneRecipeType;

public class RecipeRegistry {

    public final static IRecipeType<ShapedRecipe> SHAPED_TYPE = registerType(
        new ResourceLocation("minecraft", "crafting_shaped"),
        ShapedRecipeType.INSTANCE);
    public final static IRecipeSerializer<ShapedRecipe> SHAPED_SERIALIZER = registerSerializer(
        new ResourceLocation("minecraft", "crafting_shaped"),
        ShapedRecipeSerializer.INSTANCE);

    public final static IRecipeType<ShapelessRecipe> SHAPELESS_TYPE = registerType(
        new ResourceLocation("minecraft", "crafting_shapeless"),
        ShapelessRecipeType.INSTANCE);
    public final static IRecipeSerializer<ShapelessRecipe> SHAPELESS_SERIALIZER = registerSerializer(
        new ResourceLocation("minecraft", "crafting_shapeless"),
        ShapelessRecipeSerializer.INSTANCE);

    public final static IRecipeType<FuelRecipe> FUEL_TYPE = registerType(
        new ResourceLocation("minecraft", "fuel"),
        FuelType.INSTANCE);
    public final static IRecipeSerializer<FuelRecipe> FUEL_SERIALIZER = registerSerializer(
        new ResourceLocation("minecraft", "fuel"),
        FuelSerializer.INSTANCE);

    public final static IRecipeType<SmeltingRecipe> SMELTING_TYPE = registerType(
        new ResourceLocation("minecraft", "smelting"),
        SmeltingType.INSTANCE);
    public final static IRecipeSerializer<SmeltingRecipe> SMELTING_SERIALIZER = registerSerializer(
        new ResourceLocation("minecraft", "smelting"),
        SmeltingSerializer.INSTANCE);

    public final static IRecipeType<NoneRecipe> NONE_TYPE = registerType(
        new ResourceLocation("okcore", "none"),
        NoneRecipeType.INSTANCE);
    public final static IRecipeSerializer<NoneRecipe> NONE_SERIALIZER = registerSerializer(
        new ResourceLocation("okcore", "none"),
        NoneRecipeSerializer.INSTANCE);

    private static final Map<ResourceLocation, IRecipeSerializer<?>> SERIALIZER_MAPPING = new HashMap<>();
    private static final Map<ResourceLocation, IRecipeType<?>> TYPE_MAPPING = new HashMap<>();

    private static final Map<ResourceLocation, SmeltingRecipe> FURNACE_BRIDGE_MAP = new HashMap<>();

    public static <T extends IRecipeOK<?>> IRecipeType<T> registerType(ResourceLocation key) {
        return registerType(key, IRecipeType.simple(key));
    }

    public static <T extends IRecipeOK<?>> IRecipeType<T> registerType(ResourceLocation key,
        IRecipeType<T> recipeType) {
        if (recipeType == null || key == null) return null;
        if (TYPE_MAPPING != null) {
            TYPE_MAPPING.put(key, recipeType);
        }
        return recipeType;
    }

    public static <T extends IRecipeOK<?>> IRecipeSerializer<T> registerSerializer(ResourceLocation key,
        IRecipeSerializer<T> recipeSerializer) {
        if (recipeSerializer == null || key == null) return null;
        if (SERIALIZER_MAPPING != null) {
            SERIALIZER_MAPPING.put(key, recipeSerializer);
        }
        return recipeSerializer;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IRecipeOK<?>> IRecipeType<T> getType(ResourceLocation key) {
        if (key == null) return null;
        return (IRecipeType<T>) TYPE_MAPPING.get(key);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IRecipeOK<?>> IRecipeSerializer<T> getSerializer(ResourceLocation type) {
        if (type == null) return null;
        return (IRecipeSerializer<T>) SERIALIZER_MAPPING.get(type);
    }

    public static ResourceLocation getKey(IRecipeSerializer<?> serializer) {
        if (serializer == null) return null;
        for (Map.Entry<ResourceLocation, IRecipeSerializer<?>> entry : SERIALIZER_MAPPING.entrySet()) {
            if (entry.getValue() == serializer) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static IRecipeOK<?> deserialize(ResourceLocation id, JsonObject json) {
        if (!json.has("type")) {
            throw new JsonSyntaxException("Missing 'type' string inside recipe JSON for: " + id);
        }
        String typeStr = json.get("type")
            .getAsString();
        IRecipeSerializer<?> serializer = getSerializer(new ResourceLocation(typeStr));
        if (serializer == null) {
            OKCore.okLog(Level.WARN, "Missing recipe serializer for type: '{}' (Recipe ID: '{}')", typeStr, id);
            return null;
        }
        return serializer.fromJson(id, json);
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
        for (SmeltingRecipe oldRecipe : FURNACE_BRIDGE_MAP.values()) {
            ItemStack oldOutput = oldRecipe.getRecipeOutput();
            if (oldOutput == null) continue;

            Iterator<Map.Entry> smeltingIterator = mcSmeltingList.entrySet()
                .iterator();
            while (smeltingIterator.hasNext()) {
                Map.Entry entry = smeltingIterator.next();
                ItemStack valueStack = (ItemStack) entry.getValue();
                if (ItemStack.areItemStacksEqual(valueStack, oldOutput)) {
                    smeltingIterator.remove();
                }
            }

            if (mcExperienceList != null) {
                Iterator<Map.Entry> expIterator = mcExperienceList.entrySet()
                    .iterator();
                while (expIterator.hasNext()) {
                    Map.Entry entry = expIterator.next();
                    if (ItemStack.areItemStacksEqual((ItemStack) entry.getKey(), oldOutput)) {
                        expIterator.remove();
                    }
                }
            }
        }

        FURNACE_BRIDGE_MAP.clear();

        if (targetRecipes == null || targetRecipes.isEmpty()) return;

        for (IRecipeOK<?> recipe : targetRecipes) {
            if (recipe instanceof SmeltingRecipe customRecipe) {
                ResourceLocation recipeId = customRecipe.getId();
                ItemStack customOutput = customRecipe.getRecipeOutput();
                float customExp = customRecipe.getExperience();
                if (customRecipe.getIngredient() == null || customOutput == null || recipeId == null) continue;
                List<ItemStack> matchingStacks = customRecipe.getIngredient()
                    .toStacks();
                if (matchingStacks == null || matchingStacks.isEmpty()) continue;

                ItemStack representInput = matchingStacks.getFirst();
                if (representInput == null) continue;

                mcSmeltingList.put(representInput, customOutput);
                if (mcExperienceList != null) {
                    mcExperienceList.put(customOutput, customExp);
                }
                FURNACE_BRIDGE_MAP.put(recipeId, customRecipe);
            }
        }
        if (LibMods.NotEnoughItems.isModLoaded()) {
            NEIHelpers.reloadNEIFuels();
        }
    }
}
