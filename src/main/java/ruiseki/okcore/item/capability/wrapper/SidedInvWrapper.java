package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

public class SidedInvWrapper implements IItemHandlerModifiable {

    protected final ISidedInventory inv;
    protected final ForgeDirection side;

    public SidedInvWrapper(ISidedInventory inv, ForgeDirection side) {
        this.inv = inv;
        this.side = side;
    }

    public static int getSlot(ISidedInventory inv, int slot, ForgeDirection side) {
        if (inv == null || side == null) return -1;
        int[] slots = inv.getAccessibleSlotsFromSide(side.ordinal());
        if (slot >= 0 && slot < slots.length) return slots[slot];
        return -1;
    }

    @Override
    public int getSlots() {
        if (inv == null || side == null) return 0;
        int[] slots = inv.getAccessibleSlotsFromSide(side.ordinal());
        return slots != null ? slots.length : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int realSlot = getSlot(inv, slot, side);
        if (realSlot == -1) return ItemStackHelpers.EMPTY;

        ItemStack stack = inv.getStackInSlot(realSlot);
        return ItemStackHelpers.isEmpty(stack) ? ItemStackHelpers.EMPTY : stack;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (ItemStackHelpers.isEmpty(stack)) return ItemStackHelpers.EMPTY;

        int realSlot = getSlot(inv, slot, side);
        if (realSlot == -1) return stack;

        if (!inv.canInsertItem(realSlot, stack, side.ordinal()) || !inv.isItemValidForSlot(realSlot, stack)) {
            return stack;
        }

        ItemStack stackInSlot = inv.getStackInSlot(realSlot);
        int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));

        if (ItemStackHelpers.isEmpty(stackInSlot)) {
            if (stack.stackSize <= limit) {
                if (!simulate) {
                    setInventorySlotContents(realSlot, ItemStackHelpers.copy(stack));
                }
                return ItemStackHelpers.EMPTY;
            } else {
                ItemStack remainder = ItemStackHelpers.copy(stack);
                ItemStack toInsert = ItemStackHelpers.split(remainder, limit);

                if (!simulate) {
                    setInventorySlotContents(realSlot, toInsert);
                }

                return remainder;
            }
        } else {
            if (!ItemStackHelpers.canStack(stackInSlot, stack)) {
                return stack;
            }

            if (stackInSlot.stackSize >= limit) {
                return stack;
            }

            int canInsert = limit - stackInSlot.stackSize;

            if (stack.stackSize <= canInsert) {
                if (!simulate) {
                    ItemStackHelpers.grow(stackInSlot, stack.stackSize);
                    inv.markDirty();
                }
                return ItemStackHelpers.EMPTY;
            } else {
                ItemStack remainder = ItemStackHelpers.copy(stack);
                ItemStackHelpers.shrink(remainder, canInsert);

                if (!simulate) {
                    ItemStackHelpers.grow(stackInSlot, canInsert);
                    inv.markDirty();
                }
                return remainder;
            }
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        int realSlot = getSlot(inv, slot, side);
        if (realSlot != -1) {
            setInventorySlotContents(realSlot, stack);
        }
    }

    private void setInventorySlotContents(int slot, ItemStack stack) {
        inv.setInventorySlotContents(slot, ItemStackHelpers.isEmpty(stack) ? ItemStackHelpers.EMPTY : stack);
        inv.markDirty();
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStackHelpers.EMPTY;

        int realSlot = getSlot(inv, slot, side);
        if (realSlot == -1) return ItemStackHelpers.EMPTY;

        ItemStack stackInSlot = inv.getStackInSlot(realSlot);
        if (ItemStackHelpers.isEmpty(stackInSlot)) return ItemStackHelpers.EMPTY;

        if (!inv.canExtractItem(realSlot, stackInSlot, side.ordinal())) {
            return ItemStackHelpers.EMPTY;
        }

        int extracted = Math.min(amount, stackInSlot.stackSize);

        if (simulate) {
            ItemStack copy = ItemStackHelpers.copy(stackInSlot);
            if (!ItemStackHelpers.isEmpty(copy)) {
                copy.stackSize = extracted;
            }
            return copy;
        } else {
            ItemStack ret = inv.decrStackSize(realSlot, extracted);
            inv.markDirty();
            return ItemStackHelpers.isEmpty(ret) ? ItemStackHelpers.EMPTY : ret;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv != null ? inv.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (ItemStackHelpers.isEmpty(stack)) return false;
        int realSlot = getSlot(inv, slot, side);
        return realSlot != -1 && inv.isItemValidForSlot(realSlot, stack);
    }
}
