package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemHelpers;
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
        if (inventory == null) return 0;
        if (inventory instanceof ISidedInventory sidedInv) {
            int[] slots = side != null ? sidedInv.getAccessibleSlotsFromSide(side.ordinal()) : null;
            return slots != null ? slots.length : 0;
        }
        return inventory.getSizeInventory();
    }

    private int getSlotIndex(int slot) {
        if (inventory == null) return -1;
        if (inventory instanceof ISidedInventory sidedInv) {
            if (side == null) return -1;
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
        if (realSlot == -1) return ItemHelpers.EMPTY;

        ItemStack stack = inventory.getStackInSlot(realSlot);
        return ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
    }

    @Override
    public @Nullable ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        if (ItemHelpers.isEmpty(stack)) return ItemHelpers.EMPTY;

        int realSlot = getSlotIndex(slot);
        if (realSlot == -1) return stack;

        if (!inventory.isItemValidForSlot(realSlot, stack)) return stack;
        if (inventory instanceof ISidedInventory sidedInv && side != null
            && !sidedInv.canInsertItem(realSlot, stack, side.ordinal())) {
            return stack;
        }

        ItemStack existing = inventory.getStackInSlot(realSlot);
        int limit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

        if (ItemHelpers.isEmpty(existing)) {
            int accept = Math.min(stack.stackSize, limit);
            if (!simulate) {
                ItemStack copy = ItemHelpers.copyWithSize(stack, accept);
                inventory.setInventorySlotContents(realSlot, copy);
                inventory.markDirty();
            }
            if (accept >= stack.stackSize) return ItemHelpers.EMPTY;

            ItemStack remainder = ItemHelpers.copy(stack);
            ItemHelpers.shrink(remainder, accept);
            return remainder;
        }

        if (!ItemHelpers.canStack(existing, stack)) {
            return stack;
        }

        int maxInsert = limit - existing.stackSize;
        if (maxInsert <= 0) return stack;

        int accept = Math.min(stack.stackSize, maxInsert);
        if (!simulate) {
            ItemHelpers.grow(existing, accept);
            inventory.markDirty();
        }

        if (accept >= stack.stackSize) return ItemHelpers.EMPTY;

        ItemStack remainder = ItemHelpers.copy(stack);
        ItemHelpers.shrink(remainder, accept);
        return remainder;
    }

    @Override
    public @Nullable ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemHelpers.EMPTY;

        int realSlot = getSlotIndex(slot);
        if (realSlot == -1) return ItemHelpers.EMPTY;

        ItemStack existing = inventory.getStackInSlot(realSlot);
        if (ItemHelpers.isEmpty(existing)) return ItemHelpers.EMPTY;

        if (inventory instanceof ISidedInventory sidedInv && side != null
            && !sidedInv.canExtractItem(realSlot, existing, side.ordinal())) {
            return ItemHelpers.EMPTY;
        }

        int toExtract = Math.min(existing.stackSize, amount);
        ItemStack extracted = ItemHelpers.copyWithSize(existing, toExtract);

        if (!simulate) {
            ItemHelpers.shrink(existing, toExtract);
            if (ItemHelpers.isEmpty(existing)) {
                inventory.setInventorySlotContents(realSlot, ItemHelpers.EMPTY);
            } else {
                inventory.setInventorySlotContents(realSlot, existing);
            }
            inventory.markDirty();
        }

        return extracted;
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory != null ? inventory.getInventoryStackLimit() : 0;
    }
}
