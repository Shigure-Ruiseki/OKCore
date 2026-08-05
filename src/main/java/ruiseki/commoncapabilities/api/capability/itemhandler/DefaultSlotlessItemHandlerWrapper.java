package ruiseki.commoncapabilities.api.capability.itemhandler;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.item.handler.IItemHandler;

/**
 * An naive {@link ISlotlessItemHandler} wrapper around an {@link IItemHandler}.
 * This will perform a LIFO item algorithm.
 * 
 * @author rubensworks
 */
public class DefaultSlotlessItemHandlerWrapper implements ISlotlessItemHandler {

    private final IItemHandler itemHandler;

    public DefaultSlotlessItemHandlerWrapper(IItemHandler itemHandler) {
        this.itemHandler = itemHandler;
    }

    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, boolean simulate) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            stack = itemHandler.insertItem(i, stack, simulate);
            if (stack == null) {
                return null;
            }
        }
        return stack;
    }

    @Override
    public ItemStack extractItem(int amount, boolean simulate) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler.extractItem(i, amount, simulate);
            if (itemStack != null) {
                return itemStack;
            }
        }
        return null;
    }

    @Override
    public ItemStack extractItem(@Nonnull ItemStack matchStack, int matchFlags, boolean simulate) {
        boolean compareStackSize = (matchFlags & ItemMatch.STACKSIZE) > 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack itemStack = itemHandler
                .extractItem(i, compareStackSize ? matchStack.stackSize : matchStack.getMaxStackSize(), simulate);
            if (itemStack != null && ItemMatch.areItemStacksEqual(matchStack, itemStack, matchFlags)) {
                return itemStack;
            }
        }
        return null;
    }
}
