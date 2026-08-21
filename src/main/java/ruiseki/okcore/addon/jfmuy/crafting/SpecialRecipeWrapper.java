package ruiseki.okcore.addon.jfmuy.crafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.jfmuy.api.ingredients.IIngredients;
import ruiseki.jfmuy.api.ingredients.VanillaTypes;
import ruiseki.jfmuy.api.recipe.wrapper.ICraftingRecipeWrapper;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipe;

public class SpecialRecipeWrapper implements ICraftingRecipeWrapper {

    private final SpecialRecipe recipe;
    private final List<List<ItemStack>> inputs;

    public SpecialRecipeWrapper(SpecialRecipe recipe) {
        this.recipe = recipe;
        this.inputs = new ArrayList<>();

        for (Ingredient ingredient : recipe.getIngredients()) {
            if (ingredient != null && ingredient != Ingredient.EMPTY) {
                ItemStack[] matchingStacks = ingredient.getItems();
                if (matchingStacks != null && matchingStacks.length > 0) {
                    inputs.add(Arrays.asList(matchingStacks));
                }
            }
        }
    }

    public SpecialRecipe getRawRecipe() {
        return recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, inputs);
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public @Nullable ResourceLocation getRegistryName() {
        return recipe.getId();
    }
}
