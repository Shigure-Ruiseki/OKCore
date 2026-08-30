package ruiseki.okcore.item.capability.wrapper;

import java.util.Iterator;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Iterators;

import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.item.handler.EmptyHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

public class CombinedInvWrapper implements IItemHandlerModifiable, Iterable<IItemHandlerModifiable> {

    protected final IItemHandlerModifiable[] itemHandler; // the handlers
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots

    public CombinedInvWrapper(IItemHandlerModifiable... itemHandler) {
        this.itemHandler = itemHandler != null ? itemHandler : new IItemHandlerModifiable[0];
        this.baseIndex = new int[this.itemHandler.length];
        int index = 0;
        for (int i = 0; i < this.itemHandler.length; i++) {
            if (this.itemHandler[i] != null) {
                index += this.itemHandler[i].getSlots();
            }
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    // returns the handler index for the slot
    protected int getIndexForSlot(int slot) {
        if (slot < 0) {
            return -1;
        }

        for (int i = 0; i < baseIndex.length; i++) {
            if (slot - baseIndex[i] < 0) {
                return i;
            }
        }
        return -1;
    }

    protected IItemHandlerModifiable getHandlerFromIndex(int index) {
        if (index < 0 || index >= itemHandler.length || itemHandler[index] == null) {
            return EmptyHandler.INSTANCE;
        }
        return itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index) {
        if (index <= 0 || index >= baseIndex.length) {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        handler.setStackInSlot(localSlot, ItemStackHelpers.isEmpty(stack) ? ItemStackHelpers.EMPTY : stack);
    }

    @Override
    public int getSlots() {
        return slotCount;
    }

    @Override
    public @Nullable ItemStack getStackInSlot(int slot) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        ItemStack result = handler.getStackInSlot(localSlot);
        return ItemStackHelpers.isEmpty(result) ? ItemStackHelpers.EMPTY : result;
    }

    @Override
    public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        if (ItemStackHelpers.isEmpty(stack)) return ItemStackHelpers.EMPTY;

        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        ItemStack result = handler.insertItem(localSlot, stack, simulate);
        return ItemStackHelpers.isEmpty(result) ? ItemStackHelpers.EMPTY : result;
    }

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStackHelpers.EMPTY;

        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        ItemStack result = handler.extractItem(localSlot, amount, simulate);
        return ItemStackHelpers.isEmpty(result) ? ItemStackHelpers.EMPTY : result;
    }

    @Override
    public int getSlotLimit(int slot) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.getSlotLimit(localSlot);
    }

    @Override
    public boolean isItemValid(int slot, @Nullable ItemStack stack) {
        if (ItemStackHelpers.isEmpty(stack)) return false;

        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.isItemValid(localSlot, stack);
    }

    @Override
    public @NotNull Iterator<IItemHandlerModifiable> iterator() {
        return Iterators.forArray(this.itemHandler);
    }
}
