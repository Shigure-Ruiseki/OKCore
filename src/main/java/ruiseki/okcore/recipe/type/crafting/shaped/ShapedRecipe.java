package ruiseki.okcore.recipe.type.crafting.shaped;

import java.util.Map;
import java.util.Set;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.ICraftingRecipe;

public class ShapedRecipe implements ICraftingRecipe, IShapedRecipe<InventoryCrafting> {

    public static int MAX_WIDTH = 3;
    public static int MAX_HEIGHT = 3;

    /**
     * Expand the max width and height allowed in the deserializer.
     * This should be called by modders who add custom crafting tables that are larger than the vanilla 3x3.
     *
     * @param width  your max recipe width
     * @param height your max recipe height
     */
    public static void setCraftingSize(int width, int height) {
        if (MAX_WIDTH < width) MAX_WIDTH = width;
        if (MAX_HEIGHT < height) MAX_HEIGHT = height;
    }

    private final int width;
    private final int height;
    private final NonNullList<Ingredient> input;
    private final ItemStack output;
    private final ResourceLocation id;

    public ShapedRecipe(ResourceLocation id, int width, int height, NonNullList<Ingredient> input, ItemStack output) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.input = input;
        this.output = output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SHAPED_RECIPE;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.input;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= this.width && height >= this.height;
    }

    @Override
    public int getRecipeWidth() {
        return this.width;
    }

    @Override
    public int getRecipeHeight() {
        return this.height;
    }

    @Override
    public int getRecipeSize() {
        return this.input.size();
    }

    @Override
    public ItemStack assemble(InventoryCrafting inventory) {
        return this.getResultItem()
            .copy();
    }

    @Override
    public boolean matchesOK(InventoryCrafting inventory, World world) {
        int invWidth = inventory.inventoryWidth;
        int invHeight = inventory.getSizeInventory() / invWidth;

        for (int i = 0; i <= invWidth - this.width; ++i) {
            for (int j = 0; j <= invHeight - this.height; ++j) {
                if (this.checkMatch(inventory, i, j, false)) {
                    return true;
                }

                if (this.checkMatch(inventory, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkMatch(InventoryCrafting inventory, int startX, int startY, boolean mirror) {
        int invWidth = inventory.inventoryWidth;
        int invHeight = inventory.getSizeInventory() / invWidth;
        for (int x = 0; x < invWidth; ++x) {
            for (int y = 0; y < invHeight; ++y) {
                int subX = x - startX;
                int subY = y - startY;
                Ingredient target = Ingredient.EMPTY;
                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (mirror) {
                        target = input.get(width - subX - 1 + subY * width);
                    } else {
                        target = input.get(subX + subY * width);
                    }
                }

                if (!target.test(inventory.getStackInRowAndColumn(x, y))) {
                    return false;
                }
            }
        }

        return true;
    }

    @VisibleForTesting
    static String[] shrink(String... p_194134_0_) {
        int i = Integer.MAX_VALUE;
        int j = 0;
        int k = 0;
        int l = 0;

        for (int i1 = 0; i1 < p_194134_0_.length; ++i1) {
            String s = p_194134_0_[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (p_194134_0_.length == l) {
            return new String[0];
        } else {
            String[] astring = new String[p_194134_0_.length - l - k];

            for (int k1 = 0; k1 < astring.length; ++k1) {
                astring[k1] = p_194134_0_[k1 + k].substring(i, j + 1);
            }

            return astring;
        }
    }

    private static int firstNonSpace(String p_194135_0_) {
        int i;
        for (i = 0; i < p_194135_0_.length() && p_194135_0_.charAt(i) == ' '; ++i) {}

        return i;
    }

    private static int lastNonSpace(String p_194136_0_) {
        int i;
        for (i = p_194136_0_.length() - 1; i >= 0 && p_194136_0_.charAt(i) == ' '; --i) {}

        return i;
    }

    public static NonNullList<Ingredient> dissolvePattern(String[] pattern, Map<String, Ingredient> keyMap, int width,
        int height) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(width * height, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(keyMap.keySet());
        set.remove(" ");

        for (int i = 0; i < pattern.length; ++i) {
            for (int j = 0; j < pattern[i].length(); ++j) {
                String s = pattern[i].substring(j, j + 1);
                Ingredient ingredient = keyMap.get(s);
                if (ingredient == null) {
                    throw new JsonSyntaxException(
                        "Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(j + width * i, ingredient);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    public static String[] patternFromJson(JsonArray p_192407_0_) {
        String[] astring = new String[p_192407_0_.size()];
        if (astring.length > MAX_HEIGHT) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = GsonHelpers.convertToString(p_192407_0_.get(i), "pattern[" + i + "]");
                if (s.length() > MAX_WIDTH) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    public static Map<String, Ingredient> keyFromJson(JsonObject jsonObject) {
        Map<String, Ingredient> map = Maps.newHashMap();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getKey()
                .length() != 1) {
                throw new JsonSyntaxException(
                    "Invalid key entry: '" + entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put(entry.getKey(), Ingredient.fromJson(entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    public static ItemStack itemFromJson(JsonObject jsonObject) {
        if (jsonObject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            return RecipeRegistry.getItemStack(jsonObject, true);
        }
    }
}
