package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.helper.ItemHelpers;
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
        if (realSlot == -1) return ItemHelpers.EMPTY;

        ItemStack stack = inv.getStackInSlot(realSlot);
        return ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (ItemHelpers.isEmpty(stack)) return ItemHelpers.EMPTY;

        int realSlot = getSlot(inv, slot, side);
        if (realSlot == -1) return stack;

        if (!inv.canInsertItem(realSlot, stack, side.ordinal()) || !inv.isItemValidForSlot(realSlot, stack)) {
            return stack;
        }

        ItemStack stackInSlot = inv.getStackInSlot(realSlot);
        int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));

        if (ItemHelpers.isEmpty(stackInSlot)) {
            if (stack.stackSize <= limit) {
                if (!simulate) {
                    setInventorySlotContents(realSlot, ItemHelpers.copy(stack));
                }
                return ItemHelpers.EMPTY;
            } else {
                ItemStack remainder = ItemHelpers.copy(stack);
                ItemStack toInsert = ItemHelpers.split(remainder, limit);

                if (!simulate) {
                    setInventorySlotContents(realSlot, toInsert);
                }

                return remainder;
            }
        } else {
            if (!ItemHelpers.canStack(stackInSlot, stack)) {
                return stack;
            }

            if (stackInSlot.stackSize >= limit) {
                return stack;
            }

            int canInsert = limit - stackInSlot.stackSize;

            if (stack.stackSize <= canInsert) {
                if (!simulate) {
                    ItemHelpers.grow(stackInSlot, stack.stackSize);
                    inv.markDirty();
                }
                return ItemHelpers.EMPTY;
            } else {
                ItemStack remainder = ItemHelpers.copy(stack);
                ItemHelpers.shrink(remainder, canInsert);

                if (!simulate) {
                    ItemHelpers.grow(stackInSlot, canInsert);
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
        inv.setInventorySlotContents(slot, ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack);
        inv.markDirty();
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemHelpers.EMPTY;

        int realSlot = getSlot(inv, slot, side);
        if (realSlot == -1) return ItemHelpers.EMPTY;

        ItemStack stackInSlot = inv.getStackInSlot(realSlot);
        if (ItemHelpers.isEmpty(stackInSlot)) return ItemHelpers.EMPTY;

        if (!inv.canExtractItem(realSlot, stackInSlot, side.ordinal())) {
            return ItemHelpers.EMPTY;
        }

        int extracted = Math.min(amount, stackInSlot.stackSize);

        if (simulate) {
            ItemStack copy = ItemHelpers.copy(stackInSlot);
            if (!ItemHelpers.isEmpty(copy)) {
                copy.stackSize = extracted;
            }
            return copy;
        } else {
            ItemStack ret = inv.decrStackSize(realSlot, extracted);
            inv.markDirty();
            return ItemHelpers.isEmpty(ret) ? ItemHelpers.EMPTY : ret;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return inv != null ? inv.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) return false;
        int realSlot = getSlot(inv, slot, side);
        return realSlot != -1 && inv.isItemValidForSlot(realSlot, stack);
    }
}
