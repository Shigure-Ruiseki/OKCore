package ruiseki.okcore.item.capability.wrapper;

import java.util.Objects;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

public class InvWrapper implements IItemHandlerModifiable {

    private final IInventory inv;

    public InvWrapper(IInventory inv) {
        this.inv = Objects.requireNonNull(inv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvWrapper that = (InvWrapper) o;

        return getInv().equals(that.getInv());
    }

    @Override
    public int hashCode() {
        return getInv().hashCode();
    }

    @Override
    public int getSlots() {
        return getInv().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        ItemStack stack = getInv().getStackInSlot(slot);
        return ItemStackHelpers.isEmpty(stack) ? ItemStackHelpers.EMPTY : stack;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (ItemStackHelpers.isEmpty(stack)) return ItemStackHelpers.EMPTY;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        int m;
        if (!ItemStackHelpers.isEmpty(stackInSlot)) {
            if (stackInSlot.stackSize >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) return stack;

            if (!ItemHandlerHelpers.canItemStacksStack(stack, stackInSlot)) return stack;

            if (!getInv().isItemValidForSlot(slot, stack)) return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.stackSize;

            if (stack.stackSize <= m) {
                if (!simulate) {
                    ItemStack copy = ItemStackHelpers.copy(stack);
                    ItemStackHelpers.grow(copy, stackInSlot.stackSize);
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                }

                return ItemStackHelpers.EMPTY;
            } else {
                stack = ItemStackHelpers.copy(stack);
                if (!simulate) {
                    ItemStack copy = ItemStackHelpers.split(stack, m);
                    ItemStackHelpers.grow(copy, stackInSlot.stackSize);
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                } else {
                    ItemStackHelpers.shrink(stack, m);
                }
                return stack;
            }
        } else {
            if (!getInv().isItemValidForSlot(slot, stack)) return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.stackSize) {
                stack = ItemStackHelpers.copy(stack);
                if (!simulate) {
                    getInv().setInventorySlotContents(slot, ItemStackHelpers.split(stack, m));
                    getInv().markDirty();
                } else {
                    ItemStackHelpers.shrink(stack, m);
                }
                return stack;
            } else {
                if (!simulate) {
                    getInv().setInventorySlotContents(slot, ItemStackHelpers.copy(stack));
                    getInv().markDirty();
                }
                return ItemStackHelpers.EMPTY;
            }
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStackHelpers.EMPTY;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        if (ItemStackHelpers.isEmpty(stackInSlot)) return ItemStackHelpers.EMPTY;

        if (simulate) {
            if (stackInSlot.stackSize < amount) {
                return ItemStackHelpers.copy(stackInSlot);
            } else {
                ItemStack copy = ItemStackHelpers.copy(stackInSlot);
                if (!ItemStackHelpers.isEmpty(copy)) {
                    copy.stackSize = amount;
                }
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.stackSize, amount);

            ItemStack decrStackSize = getInv().decrStackSize(slot, m);
            getInv().markDirty();
            return ItemStackHelpers.isEmpty(decrStackSize) ? ItemStackHelpers.EMPTY : decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        getInv().setInventorySlotContents(slot, ItemStackHelpers.isEmpty(stack) ? ItemStackHelpers.EMPTY : stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getInv().getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (ItemStackHelpers.isEmpty(stack)) return false;
        return getInv().isItemValidForSlot(slot, stack);
    }

    @Deprecated
    public IInventory getInv() {
        return inv;
    }

    @Override
    public boolean isSlotFromInventory(int index, IInventory inventory, int invIndex) {
        return inventory == this.inv && index == invIndex && invIndex >= 0 && invIndex < inv.getSizeInventory();
    }
}
