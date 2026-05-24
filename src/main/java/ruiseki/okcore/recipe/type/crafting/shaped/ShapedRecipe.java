package ruiseki.okcore.recipe.type.crafting.shaped;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;

import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.json.item.CompoundItemMaterial;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeRegistry;

public class ShapedRecipe implements IShapedRecipe<InventoryCrafting> {

    private static final int MAX_CRAFT_GRID_WIDTH = 3;
    private static final int MAX_CRAFT_GRID_HEIGHT = 3;

    private final ResourceLocation id;
    private final ItemStack output;
    private final CompoundItemMaterial[] input;
    private final Map<Character, CompoundItemMaterial> keyMap;
    private final String[] pattern;
    private int width = 0;
    private int height = 0;
    private boolean mirrored = true;

    public ShapedRecipe(ResourceLocation id, ItemStack result, String[] pattern,
        Map<Character, CompoundItemMaterial> keyMap) {
        this.id = id;
        this.output = result.copy();
        this.pattern = pattern;
        this.keyMap = keyMap;

        this.height = pattern.length;
        for (String s : pattern) {
            this.width = Math.max(this.width, s.length());
        }

        this.input = new CompoundItemMaterial[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < pattern[i].length(); j++) {
                char chr = pattern[i].charAt(j);
                this.input[j + i * width] = keyMap.get(chr);
            }
        }
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
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.getSerializer(SHAPED);
    }

    @Override
    public int getRecipeSize() {
        return this.input.length;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return this.output;
    }

    @Override
    public ItemStack getCraftingResultOK(InventoryCrafting inventory) {
        return this.output.copy();
    }

    public Map<Character, CompoundItemMaterial> getKeyMap() {
        return this.keyMap;
    }

    public String[] getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matchesOK(InventoryCrafting inventory, World world) {
        for (int x = 0; x <= MAX_CRAFT_GRID_WIDTH - width; x++) {
            for (int y = 0; y <= MAX_CRAFT_GRID_HEIGHT - height; ++y) {
                if (checkMatch(inventory, x, y, false)) {
                    return true;
                }
                if (mirrored && checkMatch(inventory, x, y, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkMatch(InventoryCrafting inv, int startX, int startY, boolean mirror) {
        for (int x = 0; x < MAX_CRAFT_GRID_WIDTH; x++) {
            for (int y = 0; y < MAX_CRAFT_GRID_HEIGHT; y++) {
                int subX = x - startX;
                int subY = y - startY;
                CompoundItemMaterial targetMaterial = null;

                if (subX >= 0 && subY >= 0 && subX < width && subY < height) {
                    if (mirror) {
                        targetMaterial = input[width - subX - 1 + subY * width];
                    } else {
                        targetMaterial = input[subX + subY * width];
                    }
                }

                ItemStack slotStack = inv.getStackInRowAndColumn(x, y);
                if (targetMaterial == null || targetMaterial.isEmpty()) {
                    if (slotStack != null) return false;
                } else {
                    if (!targetMaterial.test(slotStack)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ShapedRecipe setMirrored(boolean mirror) {
        this.mirrored = mirror;
        return this;
    }
}
