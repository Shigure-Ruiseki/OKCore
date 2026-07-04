package ruiseki.okcore.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.recipe.RecipeRegistry;

public class CraftingHelpers {

    public static <C extends IInventory, T extends IRecipeOK<C>> Optional<T> getRecipeFor(
        ResourceLocation recipeTypeKey, C inventory, World world) {

        IRecipeType<T> type = RecipeRegistry.getType(recipeTypeKey);
        if (type == null) return Optional.empty();

        return RecipeManager.getManager()
            .getRecipeFor(type, inventory, world);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> Collection<T> getRecipesFor(
        ResourceLocation recipeTypeKey, C inventory, World world) {

        IRecipeType<T> type = RecipeRegistry.getType(recipeTypeKey);
        if (type == null) return Collections.emptyList();

        return RecipeManager.getManager()
            .getRecipesFor(type, inventory, world);
    }

    public static <C extends IInventory, T extends IRecipeOK<C>> NonNullList<ItemStack> getRemainingItemsFor(
        ResourceLocation recipeTypeKey, C inventory, World world) {

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
