package ruiseki.okcore.recipe.type.crafting.shaped;

import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeSerializer.SHAPED_RECIPE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeRegistries;

public class ShapedRecipesOK extends ShapedOreRecipe implements IShapedRecipe<InventoryCrafting> {

    private final ResourceLocation id;
    private final String[] pattern;
    private final Map<Character, Object> keyMap;

    public ShapedRecipesOK(ResourceLocation id, ItemStack result, String[] pattern, Map<Character, Object> keys) {
        super(result, buildRecipeArgs(pattern, keys));
        this.id = id;
        this.pattern = pattern;
        this.keyMap = keys;
    }

    private static Object[] buildRecipeArgs(String[] pattern, Map<Character, Object> keys) {
        List<Object> args = new ArrayList<>();
        Collections.addAll(args, pattern);
        for (Map.Entry<Character, Object> entry : keys.entrySet()) {
            args.add(entry.getKey());
            args.add(entry.getValue());
        }
        return args.toArray();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public String[] getPattern() {
        return pattern;
    }

    public Map<Character, Object> getKeyMap() {
        return keyMap;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistries.getSerializer(SHAPED_RECIPE);
    }

    @Override
    public boolean matches(InventoryCrafting inventory, World world) {
        return super.matches(inventory, world);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack stack = super.getCraftingResult(inventory);
        OKCore.okLog(stack.toString());
        return stack;
    }

    @Override
    public int getRecipeWidth() {
        return width;
    }

    @Override
    public int getRecipeHeight() {
        return height;
    }
}
