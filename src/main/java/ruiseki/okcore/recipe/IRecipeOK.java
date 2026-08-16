package ruiseki.okcore.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public interface IRecipeOK<C extends IInventory> extends IRecipe {

    boolean matchesOK(C inventory, World world);

    ItemStack assemble(C inventory);

    boolean canCraftInDimensions(int width, int height);

    ItemStack getResultItem();

    default NonNullList<ItemStack> getRemainingItems(C inventory) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventory.getSizeInventory(), null);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inventory.getStackInSlot(i);
            if (item != null && item.getItem() != null
                && item.getItem()
                    .hasContainerItem(item)) {
                nonnulllist.set(
                    i,
                    item.getItem()
                        .getContainerItem(item));
            }
        }

        return nonnulllist;
    }

    default NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    default boolean isSpecial() {
        return false;
    }

    ResourceLocation getId();

    IRecipeSerializer<?> getSerializer();

    IRecipeType<?> getType();

    @SuppressWarnings("unchecked")
    @Override
    default boolean matches(InventoryCrafting inv, World world) {
        try {
            return this.matchesOK((C) inv, world);
        } catch (ClassCastException e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    default ItemStack getCraftingResult(InventoryCrafting inv) {
        try {
            return this.assemble((C) inv);
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    default ItemStack getRecipeOutput() {
        return getResultItem();
    }
}
