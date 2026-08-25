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

        NonNullList<Ingredient> ingredients = recipe.getIngredients();

        for (Ingredient ingredient : ingredients) {
            if (ingredient != null && ingredient != Ingredient.EMPTY) {
                ItemStack[] matchingStacks = ingredient.getItems();
                if (matchingStacks != null && matchingStacks.length > 0) {
                    inputs.add(Arrays.asList(matchingStacks));
                } else {
                    inputs.add(new ArrayList<>());
                }
            } else {
                inputs.add(new ArrayList<>());
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
