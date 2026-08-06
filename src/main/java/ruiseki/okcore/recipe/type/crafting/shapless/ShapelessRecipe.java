package ruiseki.okcore.recipe.type.crafting.shapless;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import it.unimi.dsi.fastutil.ints.IntList;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.RecipeItemHelpers;
import ruiseki.okcore.recipe.RecipeMatcher;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.ingredient.Ingredient;
import ruiseki.okcore.recipe.type.crafting.ICraftingRecipe;

public class ShapelessRecipe implements ICraftingRecipe {

    private final ResourceLocation id;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;
    private final boolean isSimple;

    public ShapelessRecipe(ResourceLocation id, ItemStack result, NonNullList<Ingredient> ingredients) {
        this.id = id;
        this.result = result;
        this.ingredients = ingredients;
        this.isSimple = ingredients.stream()
            .allMatch(Ingredient::isSimple);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SHAPELESS_RECIPE;
    }

    @Override
    public int getRecipeSize() {
        return this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matchesOK(InventoryCrafting inventory, World world) {
        RecipeItemHelpers recipeitemhelper = new RecipeItemHelpers();
        List<ItemStack> inputs = new ArrayList<>();
        int i = 0;

        for (int j = 0; j < inventory.getSizeInventory(); ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (itemstack != null) {
                ++i;
                if (isSimple) recipeitemhelper.accountStack(itemstack, 1);
                else inputs.add(itemstack);
            }
        }

        return i == this.ingredients.size() && (isSimple ? recipeitemhelper.canCraft(this, (IntList) null)
            : RecipeMatcher.findMatches(inputs, this.ingredients) != null);
    }

    public ItemStack assemble(InventoryCrafting inventory) {
        return this.result.copy();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.ingredients.size();
    }
}
