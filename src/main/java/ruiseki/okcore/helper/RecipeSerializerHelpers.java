package ruiseki.okcore.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.okcore.recipe.ingredient.Ingredient;

/**
 * Helpers related to recipe serialization.
 *
 * @author rubensworks
 */
public class RecipeSerializerHelpers {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    public static Ingredient getJsonIngredient(JsonObject json, String key, boolean required) {
        JsonElement element = json.get(key);
        if (element == null) {
            if (required) {
                throw new JsonSyntaxException(
                    "Missing " + key + ", expected to find an ingredient object or string value");
            } else {
                return Ingredient.EMPTY;
            }
        } else if (element.isJsonObject()) {
            return Ingredient.fromJson(json.getAsJsonObject(key));
        } else if (element.isJsonArray()) {
            return Ingredient.fromJson(json.getAsJsonArray(key));
        } else {
            String itemName = element.getAsString();
            Item item = getItemFromRegistry(itemName);
            if (item == null) {
                throw new JsonSyntaxException("Item: " + itemName + " does not exist");
            }
            return Ingredient.of(new ItemStack(item));
        }
    }

    @Deprecated
    public static ItemStack getJsonItemStackOrTag(JsonObject json, boolean required) {
        return getJsonItemStackOrTag(json, required, Collections.emptyList());
    }

    public static ItemStack getJsonItemStackOrTag(JsonObject json, boolean required, List<String> modPriorities) {
        if (json.has("tag")) {
            return getJsonItemStackFromTag(json, "tag", modPriorities);
        }
        return getJsonItemStack(json, "item", required);
    }

    public static ItemStack getJsonItemStack(JsonObject json, String key, boolean required) {
        JsonElement element = json.get(key);
        if (element == null) {
            if (required) {
                throw new JsonSyntaxException("Missing " + key + ", expected to find an item object or string value");
            } else {
                return null;
            }
        } else if (element.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject(key);
            String itemName = obj.get("item")
                .getAsString();
            Item item = getItemFromRegistry(itemName);
            if (item == null) {
                throw new JsonSyntaxException("Item: " + itemName + " does not exist");
            }
            int amount = obj.has("count") ? obj.get("count")
                .getAsInt() : 1;
            int meta = obj.has("data") ? obj.get("data")
                .getAsInt() : 0;
            return new ItemStack(item, amount, meta);
        } else {
            String itemName = element.getAsString();
            Item item = getItemFromRegistry(itemName);
            if (item == null) {
                throw new JsonSyntaxException("Item: " + itemName + " does not exist");
            }
            return new ItemStack(item);
        }
    }

    @Deprecated
    public static ItemStack getJsonItemStackFromTag(JsonObject json, String key) {
        return getJsonItemStackFromTag(json, key, Collections.<String>emptyList());
    }

    public static ItemStack getJsonItemStackFromTag(JsonObject json, String key, List<String> modPriorities) {
        String oreName = json.get(key)
            .getAsString();
        Collection<ItemStack> matchingStacks = Ingredient.valueFromJson(json)
            .getItems();

        // Safely check if the tag resolution returned any matching items
        if (matchingStacks == null || matchingStacks.isEmpty()) {
            ruiseki.okcore.OKCore.okLog(
                org.apache.logging.log4j.Level.WARN,
                "No OreDictionary/Tag entry found for tag key '{}' (value: '{}')",
                key,
                oreName);
            return null;
        }

        final Map<String, Integer> modPriorityIndex = Maps.newHashMap();
        for (int i = 0; i < modPriorities.size(); i++) {
            modPriorityIndex.put(modPriorities.get(i), i);
        }

        ItemStack outputStack = Collections.min(matchingStacks, new Comparator<ItemStack>() {

            @Override
            public int compare(ItemStack o1, ItemStack o2) {
                String mod1 = getModId(o1);
                String mod2 = getModId(o2);

                int p1 = modPriorityIndex.getOrDefault(mod1, Integer.MAX_VALUE);
                int p2 = modPriorityIndex.getOrDefault(mod2, Integer.MAX_VALUE);

                return Integer.compare(p1, p2);
            }
        })
            .copy();

        int count = 1;
        if (json.has("count")) {
            count = json.get("count")
                .getAsInt();
        } else if (json.has("amount")) {
            count = json.get("amount")
                .getAsInt();
        }
        outputStack.stackSize = count;

        return outputStack;
    }

    public static FluidStack deserializeFluidStack(JsonObject json, boolean readNbt) {
        if (json.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            String fluidName = json.get("fluid")
                .getAsString();

            if (fluidName.contains(":")) {
                fluidName = fluidName.split(":")[1];
            }

            Fluid fluid = FluidRegistry.getFluid(fluidName);
            if (fluid == null) {
                throw new JsonParseException("Unknown fluid '" + fluidName + "'");
            } else {
                int amount = json.has("amount") ? json.get("amount")
                    .getAsInt() : 1000;
                FluidStack fluidStack = new FluidStack(fluid, amount);

                if (readNbt && json.has("nbt")) {
                    try {
                        JsonElement element = json.get("nbt");
                        String nbtString = element.isJsonObject() ? GSON.toJson(element) : element.getAsString();
                        NBTBase parsedNbt = JsonToNBT.func_150315_a(nbtString);
                        if (parsedNbt instanceof NBTTagCompound) {
                            fluidStack.tag = (NBTTagCompound) parsedNbt;
                        }
                    } catch (NBTException e) {
                        throw new JsonSyntaxException("Invalid NBT Entry: " + e.getMessage());
                    }
                }
                return fluidStack;
            }
        }
    }

    public static FluidStack getJsonFluidStack(JsonObject json, String key, boolean required) {
        JsonElement element = json.get(key);
        if (element == null) {
            if (required) {
                throw new JsonSyntaxException("Missing " + key + ", expected to find a fluid object or string value");
            } else {
                return null;
            }
        } else if (element.isJsonObject()) {
            return deserializeFluidStack(json.getAsJsonObject(key), true);
        } else {
            String fluidName = element.getAsString();
            if (fluidName.contains(":")) {
                fluidName = fluidName.split(":")[1];
            }
            Fluid fluid = FluidRegistry.getFluid(fluidName);
            if (fluid == null) {
                throw new JsonParseException("Unknown fluid '" + fluidName + "'");
            } else {
                return new FluidStack(fluid, 1000);
            }
        }
    }

    private static Item getItemFromRegistry(String itemName) {
        String[] parts = itemName.split(":");
        if (parts.length == 2) {
            return GameRegistry.findItem(parts[0], parts[1]);
        }
        return GameRegistry.findItem("minecraft", itemName);
    }

    private static String getModId(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return "minecraft";
        }
        GameRegistry.UniqueIdentifier id = GameRegistry.findUniqueIdentifierFor(stack.getItem());
        if (id != null && id.modId != null) {
            return id.modId;
        }
        return "minecraft";
    }
}
