package ruiseki.okcore.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.datastructure.NonNullList;

public interface IRecipeOK<C extends IInventory> extends IRecipe {

    ResourceLocation getId();

    IRecipeSerializer<?> getSerializer();

    IRecipeType<?> getType();

    boolean matches(C inventory, World world);

    ItemStack getCraftingResult(C inventory);

    default NonNullList<ItemStack> getRemainingItems(C inventory) {
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventory.getSizeInventory(), null);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack item = inventory.getStackInSlot(i);
            if (item.getItem() != null && item.getItem()
                .hasContainerItem(item)) {
                nonnulllist.set(
                    i,
                    item.getItem()
                        .getContainerItem(item));
            }
        }

        return nonnulllist;
    }

    @SuppressWarnings("unchecked")
    @Override
    default boolean matches(InventoryCrafting inv, World world) {
        if (inv instanceof InventoryCrafting) {
            return this.matches((C) inv, world);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    default ItemStack getCraftingResult(InventoryCrafting inv) {
        if (inv instanceof InventoryCrafting) {
            return this.getCraftingResult((C) inv);
        }
        return null;
    }
}
