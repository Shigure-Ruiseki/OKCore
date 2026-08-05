package ruiseki.commoncapabilities.api.capability.itemhandler;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.item.handler.IItemHandler;

/**
 * An abstract {@link ISlotlessItemHandler} wrapper around an {@link IItemHandler}.
 * 
 * @author rubensworks
 */
public abstract class SlotlessItemHandlerWrapper implements ISlotlessItemHandler {

    private final IItemHandler itemHandler;

    public SlotlessItemHandlerWrapper(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    /**
     * Get a slot in which the given ItemStack is present according to the given match flags.
     * Stacksize of the item in the slot must be below the maximum stack size,
     * so there must be room left in the slot.
     * If the stack is in no slot, return -1;
     * 
     * @param itemStack  The ItemStack to look for.
     * @param matchFlags The flags to match the given ItemStack by.
     * @return The slot in which the ItemStack is present, or -1.
     */
    protected abstract int getNonFullSlotWithItemStack(@Nonnull ItemStack itemStack, int matchFlags);

    /**
     * Get a slot in which the given ItemStack is present according to the given match flags.
     * Stacksize of the item in the slot must be larger than zero.
     * If the stack is in no slot, return -1;
     * 
     * @param itemStack  The ItemStack to look for.
     * @param matchFlags The flags to match the given ItemStack by.
     * @return The slot in which the ItemStack is present, or -1.
     */
    protected abstract int getNonEmptySlotWithItemStack(@Nonnull ItemStack itemStack, int matchFlags);

    /**
     * @return A slot with no ItemStack, -1 if no such slot is available.
     */
    protected abstract int getEmptySlot();

    /**
     * @return A slot that is not empty, -1 if no such slot is available.
     */
    protected abstract int getNonEmptySlot();

    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
        int slot = getNonFullSlotWithItemStack(stack, ItemMatch.DAMAGE | ItemMatch.NBT);
        if (slot < 0) slot = getEmptySlot();
        if (slot < 0) return stack;
        return itemHandler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int amount, boolean simulate) {
        int slot = getNonEmptySlot();
        if (slot < 0) return null;
        return itemHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public ItemStack extractItem(@Nonnull ItemStack matchStack, int matchFlags, boolean simulate) {
        int slot = getNonEmptySlotWithItemStack(matchStack, matchFlags);
        if (slot < 0) return null;
        return itemHandler.extractItem(slot, matchStack.stackSize, simulate);
    }
}
