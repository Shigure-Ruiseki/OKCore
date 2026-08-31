package ruiseki.okcore.item.capability.wrapper;

import java.util.Objects;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.helper.ItemHelpers;
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
        return ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (ItemHelpers.isEmpty(stack)) return ItemHelpers.EMPTY;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        int m;
        if (!ItemHelpers.isEmpty(stackInSlot)) {
            if (stackInSlot.stackSize >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) return stack;

            if (!ItemHandlerHelpers.canItemStacksStack(stack, stackInSlot)) return stack;

            if (!getInv().isItemValidForSlot(slot, stack)) return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.stackSize;

            if (stack.stackSize <= m) {
                if (!simulate) {
                    ItemStack copy = ItemHelpers.copy(stack);
                    ItemHelpers.grow(copy, stackInSlot.stackSize);
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                }

                return ItemHelpers.EMPTY;
            } else {
                stack = ItemHelpers.copy(stack);
                if (!simulate) {
                    ItemStack copy = ItemHelpers.split(stack, m);
                    ItemHelpers.grow(copy, stackInSlot.stackSize);
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                } else {
                    ItemHelpers.shrink(stack, m);
                }
                return stack;
            }
        } else {
            if (!getInv().isItemValidForSlot(slot, stack)) return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.stackSize) {
                stack = ItemHelpers.copy(stack);
                if (!simulate) {
                    getInv().setInventorySlotContents(slot, ItemHelpers.split(stack, m));
                    getInv().markDirty();
                } else {
                    ItemHelpers.shrink(stack, m);
                }
                return stack;
            } else {
                if (!simulate) {
                    getInv().setInventorySlotContents(slot, ItemHelpers.copy(stack));
                    getInv().markDirty();
                }
                return ItemHelpers.EMPTY;
            }
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemHelpers.EMPTY;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        if (ItemHelpers.isEmpty(stackInSlot)) return ItemHelpers.EMPTY;

        if (simulate) {
            if (stackInSlot.stackSize < amount) {
                return ItemHelpers.copy(stackInSlot);
            } else {
                ItemStack copy = ItemHelpers.copy(stackInSlot);
                if (!ItemHelpers.isEmpty(copy)) {
                    copy.stackSize = amount;
                }
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.stackSize, amount);

            ItemStack decrStackSize = getInv().decrStackSize(slot, m);
            getInv().markDirty();
            return ItemHelpers.isEmpty(decrStackSize) ? ItemHelpers.EMPTY : decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        getInv().setInventorySlotContents(slot, ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getInv().getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) return false;
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
