package ruiseki.okcore.item.capability.mfr;

import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import ruiseki.okcore.item.IItemHandler;

public class DeepStorageHandlerWrapper implements IItemHandler {

    private final IDeepStorageUnit dsu;

    public DeepStorageHandlerWrapper(IDeepStorageUnit dsu) {
        this.dsu = dsu;
    }

    @Override
    public int getSlotLimit(int slot) {
        return dsu.getMaxStoredCount();
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return dsu.getStoredItemType();
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize <= 0) return null;

        ItemStack stored = dsu.getStoredItemType();

        if (stored == null || (stored.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stored, stack))) {
            int currentCount = (stored == null) ? 0 : stored.stackSize;
            int capacity = dsu.getMaxStoredCount();
            int canAccept = capacity - currentCount;

            int accepted = Math.min(stack.stackSize, canAccept);
            if (accepted <= 0) return stack;

            if (!simulate) {
                if (stored == null) {
                    dsu.setStoredItemType(stack.copy(), accepted);
                } else {
                    dsu.setStoredItemCount(currentCount + accepted);
                }
            }

            if (accepted >= stack.stackSize) return null;
            ItemStack remainder = stack.copy();
            remainder.stackSize -= accepted;
            return remainder;
        }

        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot != 0 || amount <= 0) return null;

        ItemStack stored = dsu.getStoredItemType();
        if (stored == null || stored.stackSize <= 0) return null;

        int canExtract = Math.min(amount, stored.stackSize);
        canExtract = Math.min(canExtract, stored.getMaxStackSize());

        ItemStack result = stored.copy();
        result.stackSize = canExtract;

        if (!simulate) {
            dsu.setStoredItemCount(stored.stackSize - canExtract);
        }

        return result;
    }
}
