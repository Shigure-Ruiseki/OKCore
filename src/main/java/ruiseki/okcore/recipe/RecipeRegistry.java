package ruiseki.okcore.recipe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.ingredient.CompoundIngredient;
import ruiseki.okcore.recipe.ingredient.IIngredientSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.ingredient.NBTIngredient;
import ruiseki.okcore.recipe.ingredient.VanillaIngredientSerializer;
import ruiseki.okcore.recipe.type.cooking.AbstractCookingRecipe;
import ruiseki.okcore.recipe.type.cooking.furnace.CookingRecipeSerializer;
import ruiseki.okcore.recipe.type.cooking.furnace.FurnaceRecipe;
import ruiseki.okcore.recipe.type.crafting.ICraftingRecipe;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeSerializer;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeSerializer;

public class RecipeRegistry {

    private static final Map<ResourceLocation, IRecipeSerializer<?>> SERIALIZER_MAPPING = new HashMap<>();
    private static final Map<ResourceLocation, IRecipeType<?>> TYPE_MAPPING = new HashMap<>();
    private static final Map<ResourceLocation, AbstractCookingRecipe> FURNACE_BRIDGE_MAP = new HashMap<>();
    private static final BiMap<ResourceLocation, IIngredientSerializer<?>> ingredients = HashBiMap.create();

    public static IRecipeSerializer<ShapedRecipe> SHAPED_RECIPE;
    public static IRecipeSerializer<ShapelessRecipe> SHAPELESS_RECIPE;
    public static IRecipeSerializer<FurnaceRecipe> SMELTING_RECIPE;

    public static IRecipeType<ICraftingRecipe> CRAFTING;
    public static IRecipeType<FurnaceRecipe> SMELTING;

    static {
        SHAPED_RECIPE = registerSerializer(
            new ResourceLocation("minecraft:crafting_shaped"),
            new ShapedRecipeSerializer());
        SHAPELESS_RECIPE = registerSerializer(
            new ResourceLocation("minecraft:crafting_shapeless"),
            new ShapelessRecipeSerializer());
        SMELTING_RECIPE = registerSerializer(
            new ResourceLocation("minecraft:smelting"),
            new CookingRecipeSerializer<>(FurnaceRecipe::new, 200));

        CRAFTING = RecipeRegistry.registerType(new ResourceLocation("crafting"));
        SMELTING = RecipeRegistry.registerType(new ResourceLocation("smelting"));

        RecipeRegistry.register(new ResourceLocation("forge", "compound"), CompoundIngredient.Serializer.INSTANCE);
        RecipeRegistry.register(new ResourceLocation("forge", "nbt"), NBTIngredient.Serializer.INSTANCE);
        RecipeRegistry.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);
    }

    public static <T extends IRecipeOK<?>> IRecipeType<T> registerType(ResourceLocation key) {
        return registerType(key, IRecipeType.simple(key));
    }

    public static <S extends IRecipeType<T>, T extends IRecipeOK<?>> S registerType(ResourceLocation key, S type) {
        if (type == null || key == null) return null;
        TYPE_MAPPING.put(key, type);
        return type;
    }

    public static <S extends IRecipeSerializer<T>, T extends IRecipeOK<?>> S registerSerializer(ResourceLocation key,
        S recipeSerializer) {
        if (recipeSerializer == null || key == null) return null;
        SERIALIZER_MAPPING.put(key, recipeSerializer);
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

    public static <T extends Ingredient> IIngredientSerializer<T> register(ResourceLocation key,
        IIngredientSerializer<T> serializer) {
        if (ingredients.containsKey(key))
            throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key);
        if (ingredients.containsValue(serializer))
            throw new IllegalStateException("Duplicate recipe ingredient serializer: " + key + " " + serializer);
        ingredients.put(key, serializer);
        return serializer;
    }

    @Nullable
    public static ResourceLocation getID(IIngredientSerializer<?> serializer) {
        return ingredients.inverse()
            .get(serializer);
    }

    public static <T extends Ingredient> void toNetwork(ExtendedBuffer buffer, T ingredient) {
        @SuppressWarnings("unchecked")
        IIngredientSerializer<T> serializer = (IIngredientSerializer<T>) ingredient.getSerializer();
        ResourceLocation key = getID(serializer);
        if (key == null) throw new IllegalArgumentException(
            "Tried to serialize unregistered Ingredient: " + ingredient + " " + serializer);
        if (serializer != VanillaIngredientSerializer.INSTANCE) {
            buffer.writeVarIntToBuffer(-1);
            buffer.writeResourceLocation(key);
        }
        serializer.toNetwork(buffer, ingredient);
    }

    public static Ingredient getIngredient(ResourceLocation type, ExtendedBuffer buffer) {
        IIngredientSerializer<?> serializer = ingredients.get(type);
        if (serializer == null)
            throw new IllegalArgumentException("Can not deserialize unknown Ingredient type: " + type);
        return serializer.fromNetwork(buffer);
    }

    public static Ingredient getIngredient(JsonElement json, boolean allowEmpty) {
        if (json == null || json.isJsonNull()) throw new JsonSyntaxException("Json cannot be null");

        if (json.isJsonArray()) {
            List<Ingredient> ingredientsList = Lists.newArrayList();
            List<Ingredient> vanilla = Lists.newArrayList();
            json.getAsJsonArray()
                .forEach((ele) -> {
                    Ingredient ing = RecipeRegistry.getIngredient(ele, allowEmpty);

                    if (ing.getClass() == Ingredient.class) vanilla.add(ing);
                    else ingredientsList.add(ing);
                });

            if (!vanilla.isEmpty()) ingredientsList.add(Ingredient.merge(vanilla));

            if (ingredientsList.isEmpty()) {
                if (allowEmpty) {
                    return Ingredient.EMPTY;
                }
                throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
            }

            if (ingredientsList.size() == 1) return ingredientsList.getFirst();

            return new CompoundIngredient(ingredientsList);
        }

        if (!json.isJsonObject())
            throw new JsonSyntaxException("Expected ingredient to be an object or array of objects");

        JsonObject obj = (JsonObject) json;

        String type = GsonHelpers.getAsString(obj, "type", "minecraft:item");
        if (type.isEmpty()) throw new JsonSyntaxException("Ingredient type can not be an empty string");

        IIngredientSerializer<?> serializer = ingredients.get(new ResourceLocation(type));
        if (serializer == null) throw new JsonSyntaxException("Unknown ingredient type: " + type);

        return serializer.fromJson(obj);
    }

    public static ItemStack getItemStack(JsonObject json, boolean readNBT) {
        String itemName = GsonHelpers.getAsString(json, "item");

        Item item = GameData.getItemRegistry()
            .getObject(itemName);

        if (item == null) {
            throw new JsonSyntaxException("Unknown item '" + itemName + "'");
        }

        int count = 1;
        if (json.has("count")) {
            count = GsonHelpers.getAsInt(json, "count");
        } else if (json.has("amount")) {
            count = GsonHelpers.getAsInt(json, "amount");
        }

        int meta = 0;
        if (json.has("data")) {
            meta = GsonHelpers.getAsInt(json, "data");
        } else if (json.has("damage")) {
            meta = GsonHelpers.getAsInt(json, "damage");
        } else if (json.has("meta")) {
            meta = GsonHelpers.getAsInt(json, "meta");
        }

        ItemStack stack = new ItemStack(item, count, meta);

        if (readNBT && json.has("nbt")) {
            try {
                JsonElement element = json.get("nbt");
                NBTTagCompound nbt;

                if (element.isJsonObject()) {
                    nbt = GsonHelpers.jsonToNBT(element.getAsJsonObject());
                } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive()
                    .isString()) {
                        nbt = (NBTTagCompound) JsonToNBT.func_150315_a(element.getAsString());
                    } else {
                        throw new JsonSyntaxException(
                            "Expected 'nbt' to be a JsonObject or NBT String, was " + GsonHelpers.getType(element));
                    }
                stack.setTagCompound(nbt);
            } catch (NBTException e) {
                throw new JsonSyntaxException("Invalid NBT Entry: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new JsonSyntaxException("Error parsing NBT: " + e.getMessage(), e);
            }
        }

        return stack;
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
            if (recipe instanceof ICraftingRecipe) {
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
        for (AbstractCookingRecipe oldRecipe : FURNACE_BRIDGE_MAP.values()) {
            ItemStack oldOutput = oldRecipe.getResultItem();
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
            if (recipe instanceof AbstractCookingRecipe customRecipe) {
                ResourceLocation recipeId = customRecipe.getId();
                ItemStack customOutput = customRecipe.getResultItem();
                float customExp = customRecipe.getExperience();
                if (customRecipe.getIngredient() == null || customOutput == null || recipeId == null) continue;
                List<ItemStack> matchingStacks = List.of(
                    customRecipe.getIngredient()
                        .getItems());
                if (matchingStacks.isEmpty()) continue;

                ItemStack representInput = matchingStacks.getFirst();
                if (representInput == null) continue;

                mcSmeltingList.put(representInput, customOutput);
                if (mcExperienceList != null) {
                    mcExperienceList.put(customOutput, customExp);
                }
                FURNACE_BRIDGE_MAP.put(recipeId, customRecipe);
            }
        }
    }
}
