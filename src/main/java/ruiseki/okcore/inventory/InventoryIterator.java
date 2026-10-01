package ruiseki.okcore.inventory;

import java.util.Iterator;

import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

/**
 * Iterate over an inventory.
 * 
 * @author rubensworks
 *
 */
public class InventoryIterator implements Iterator<ItemStack> {

    private final IItemHandlerModifiable inventory;
    private int i;

    public InventoryIterator(IItemHandlerModifiable inventory) {
        this.inventory = inventory;
    }

    @Override
    public boolean hasNext() {
        return i < inventory.getSlots();
    }

    @Override
    public ItemStack next() {
        return inventory.getStackInSlot(i++);
    }

    /**
     * Get the next item indexed.
     * 
     * @return The indexed item.
     */
    public Pair<Integer, ItemStack> nextIndexed() {
        return Pair.of(i, next());
    }

    @Override
    public void remove() {
        if (i - 1 >= 0 && i - 1 < inventory.getSlots()) inventory.setStackInSlot(i - 1, ItemHelpers.EMPTY);
    }

    /**
     * Replaces the itemstack on the position of the last returned itemstack.
     * 
     * @param itemStack The itemstack to place.
     */
    public void replace(ItemStack itemStack) {
        if (i - 1 >= 0 && i - 1 < inventory.getSlots()) inventory.setStackInSlot(i - 1, itemStack);
    }

}
