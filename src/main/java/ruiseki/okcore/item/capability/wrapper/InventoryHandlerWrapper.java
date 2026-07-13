package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.item.handler.IItemHandler;

public class InventoryHandlerWrapper implements IItemHandler {

    public final IInventory inventory;
    public final ForgeDirection side;

    public InventoryHandlerWrapper(IInventory inventory, ForgeDirection side) {
        this.inventory = inventory;
        this.side = side;
    }

    @Override
    public int getSlots() {
        if (inventory instanceof ISidedInventory sidedInv) {
            int[] slots = sidedInv.getAccessibleSlotsFromSide(side.ordinal());
            return slots != null ? slots.length : 0;
        }
        return inventory.getSizeInventory();
    }

    private int getSlotIndex(int slot) {
        if (inventory instanceof ISidedInventory sidedInv) {
            int[] slots = sidedInv.getAccessibleSlotsFromSide(side.ordinal());
            if (slots == null || slot < 0 || slot >= slots.length) {
                return -1;
            }
            return slots[slot];
        }
        return slot;
    }

    @Override
    public @Nullable ItemStack getStackInSlot(int slot) {
        int realSlot = getSlotIndex(slot);
        if (realSlot == -1) return null;
        return inventory.getStackInSlot(realSlot);
    }

    @Override
    public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize <= 0) return null;

        int realSlot = getSlotIndex(slot);
        if (realSlot == -1) return stack;

        if (!inventory.isItemValidForSlot(realSlot, stack)) return stack;
        if (inventory instanceof ISidedInventory sidedInv && !sidedInv.canInsertItem(realSlot, stack, side.ordinal())) {
            return stack;
        }

        ItemStack existing = inventory.getStackInSlot(realSlot);
        int limit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

        if (existing == null) {
            int accept = Math.min(stack.stackSize, limit);
            if (!simulate) {
                ItemStack copy = stack.copy();
                copy.stackSize = accept;
                inventory.setInventorySlotContents(realSlot, copy);
                inventory.markDirty();
            }
            if (accept >= stack.stackSize) return null;

            ItemStack remainder = stack.copy();
            remainder.stackSize -= accept;
            return remainder;
        }

        if (!existing.isItemEqual(stack) || !ItemStack.areItemStackTagsEqual(existing, stack)) {
            return stack;
        }

        int maxInsert = limit - existing.stackSize;
        if (maxInsert <= 0) return stack;

        int accept = Math.min(stack.stackSize, maxInsert);
        if (!simulate) {
            existing.stackSize += accept;
            inventory.markDirty();
        }

        if (accept >= stack.stackSize) return null;

        ItemStack remainder = stack.copy();
        remainder.stackSize -= accept;
        return remainder;
    }

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return null;

        int realSlot = getSlotIndex(slot);
        if (realSlot == -1) return null;

        ItemStack existing = inventory.getStackInSlot(realSlot);
        if (existing == null || existing.stackSize <= 0) return null;

        if (inventory instanceof ISidedInventory sidedInv
            && !sidedInv.canExtractItem(realSlot, existing, side.ordinal())) {
            return null;
        }

        int toExtract = Math.min(existing.stackSize, amount);
        ItemStack extracted = existing.copy();
        extracted.stackSize = toExtract;

        if (!simulate) {
            existing.stackSize -= toExtract;
            if (existing.stackSize <= 0) {
                inventory.setInventorySlotContents(realSlot, null);
            } else {
                inventory.setInventorySlotContents(realSlot, existing);
            }
            inventory.markDirty();
        }

        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getInventoryStackLimit();
    }
}
