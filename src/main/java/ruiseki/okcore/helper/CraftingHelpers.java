package ruiseki.okcore.helper;

import static ruiseki.okcore.recipe.type.cooking.fuel.FuelType.FUEL;
import static ruiseki.okcore.recipe.type.cooking.furnace.SmeltingType.SMELTING;
import static ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipeType.SHAPED;
import static ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipeType.SHAPELESS;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;
import ruiseki.okcore.recipe.type.cooking.fuel.FuelRecipe;
import ruiseki.okcore.recipe.type.cooking.furnace.SmeltingRecipe;
import ruiseki.okcore.recipe.type.crafting.shaped.ShapedRecipe;
import ruiseki.okcore.recipe.type.crafting.shapless.ShapelessRecipe;

public class CraftingHelpers {

    public static Collection<ShapedRecipe> getShapedRecipes() {
        IRecipeType<ShapedRecipe> type = RecipeRegistry.getType(SHAPED);
        return type == null ? Collections.emptyList()
            : RecipeManager.getManager()
                .getRecipesByType(type);
    }

    public static Collection<ShapelessRecipe> getShapelessRecipes() {
        IRecipeType<ShapelessRecipe> type = RecipeRegistry.getType(SHAPELESS);
        return type == null ? Collections.emptyList()
            : RecipeManager.getManager()
                .getRecipesByType(type);
    }

    public static Collection<SmeltingRecipe> getSmeltingRecipes() {
        IRecipeType<SmeltingRecipe> type = RecipeRegistry.getType(SMELTING);
        return type == null ? Collections.emptyList()
            : RecipeManager.getManager()
                .getRecipesByType(type);
    }

    public static Collection<FuelRecipe> getFuelRecipes() {
        IRecipeType<FuelRecipe> type = RecipeRegistry.getType(FUEL);
        return type == null ? Collections.emptyList()
            : RecipeManager.getManager()
                .getRecipesByType(type);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getRecipeFor(String recipeTypeKey,
        C inventory, World world) {

        IRecipeType<T> type = RecipeRegistry.getType(recipeTypeKey);
        if (type == null) return Optional.empty();

        return RecipeManager.getManager()
            .getRecipeFor(type, inventory, world);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> Collection<T> getRecipesFor(String recipeTypeKey,
        C inventory, World world) {

        IRecipeType<T> type = RecipeRegistry.getType(recipeTypeKey);
        if (type == null) return Collections.emptyList();

        return RecipeManager.getManager()
            .getRecipesFor(type, inventory, world);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> NonNullList<ItemStack> getRemainingItemsFor(
        String recipeTypeKey, C inventory, World world) {

        IRecipeType<T> type = RecipeRegistry.getType(recipeTypeKey);
        if (type == null) {
            NonNullList<ItemStack> fallbackList = NonNullList.withSize(inventory.getSizeInventory(), null);
            for (int i = 0; i < fallbackList.size(); ++i) {
                fallbackList.set(i, inventory.getStackInSlot(i));
            }
            return fallbackList;
        }

        return RecipeManager.getManager()
            .getRemainingItemsFor(type, inventory, world);
    }
}
