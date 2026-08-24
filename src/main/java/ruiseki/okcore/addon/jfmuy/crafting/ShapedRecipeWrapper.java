package ruiseki.okcore.addon.jfmuy.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.jfmuy.api.ingredients.IIngredients;
import ruiseki.jfmuy.api.ingredients.VanillaTypes;
import ruiseki.jfmuy.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;

public class ShapedRecipeWrapper implements IShapedCraftingRecipeWrapper {

    private final ShapedRecipe recipe;
    private final List<List<ItemStack>> inputs;

    public ShapedRecipeWrapper(ShapedRecipe recipe) {
        this.recipe = recipe;
        this.inputs = new ArrayList<>();

        int width = recipe.getRecipeWidth();
        int height = recipe.getRecipeHeight();
        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        int gridSlots = Math.max(9, width * height);
        for (int i = 0; i < gridSlots; i++) {
            inputs.add(new ArrayList<>());
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int ingredientIndex = col + row * width;
                if (ingredientIndex < ingredients.size()) {
                    Ingredient ingredient = ingredients.get(ingredientIndex);
                    int slotIndex = col + row * 3;
                    if (slotIndex < inputs.size() && ingredient != null && ingredient != Ingredient.EMPTY) {
                        ItemStack[] matchingStacks = ingredient.getItems();
                        if (matchingStacks != null && matchingStacks.length > 0) {
                            inputs.set(slotIndex, Arrays.asList(matchingStacks));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public int getWidth() {
        return recipe.getRecipeWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getRecipeHeight();
    }

    @Override
    public @Nullable ResourceLocation getRegistryName() {
        return recipe.getId();
    }
}
