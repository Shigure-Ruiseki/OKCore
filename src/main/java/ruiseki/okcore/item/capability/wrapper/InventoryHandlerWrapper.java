package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

public class InventoryHandlerWrapper implements IItemHandlerModifiable {

    public final IInventory inventory;
    public final ForgeDirection side;

    public InventoryHandlerWrapper(IInventory inventory, @Nullable ForgeDirection side) {
        this.inventory = inventory;
        this.side = side;
    }

    @Override
    public int getSlots() {
        if (inventory == null) return 0;
        if (inventory instanceof ISidedInventory sidedInv && side != null) {
            int[] slots = sidedInv.getAccessibleSlotsFromSide(side.ordinal());
            return slots != null ? slots.length : 0;
        }
        return inventory.getSizeInventory();
    }

    private int getSlotIndex(int slot) {
        if (inventory == null) return -1;
        if (inventory instanceof ISidedInventory sidedInv && side != null) {
            int[] slots = sidedInv.getAccessibleSlotsFromSide(side.ordinal());
            if (slots == null || slot < 0 || slot >= slots.length) {
                return -1;
            }
            return slots[slot];
        }
        return slot >= 0 && slot < inventory.getSizeInventory() ? slot : -1;
    }

    @Override
    public @Nullable ItemStack getStackInSlot(int slot) {
        int realSlot = getSlotIndex(slot);
        if (realSlot == -1) return ItemHelpers.EMPTY;

        ItemStack stack = inventory.getStackInSlot(realSlot);
        return ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
    }

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        int realSlot = getSlotIndex(slot);
        if (realSlot != -1) {
            inventory.setInventorySlotContents(realSlot, ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack);
            inventory.markDirty();
        }
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
                inventory.setInventorySlotContents(realSlot, ItemHelpers.copyWithSize(stack, accept));
                inventory.markDirty();
            }
            if (accept >= stack.stackSize) return ItemHelpers.EMPTY;

            return ItemHelpers.copyWithSize(stack, stack.stackSize - accept);
        }

        if (!ItemHelpers.canStack(existing, stack)) {
            return stack;
        }

        int maxInsert = limit - existing.stackSize;
        if (maxInsert <= 0) return stack;

        int accept = Math.min(stack.stackSize, maxInsert);
        if (!simulate) {
            ItemStack newStack = ItemHelpers.copyWithSize(existing, existing.stackSize + accept);
            inventory.setInventorySlotContents(realSlot, newStack);
            inventory.markDirty();
        }

        if (accept >= stack.stackSize) return ItemHelpers.EMPTY;

        return ItemHelpers.copyWithSize(stack, stack.stackSize - accept);
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

        if (simulate) {
            return ItemHelpers.copyWithSize(existing, toExtract);
        } else {
            ItemStack extracted = inventory.decrStackSize(realSlot, toExtract);
            inventory.markDirty();
            return ItemHelpers.isEmpty(extracted) ? ItemHelpers.EMPTY : extracted;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory != null ? inventory.getInventoryStackLimit() : 0;
    }

    @Override
    public boolean isItemValid(int slot, @Nullable ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) return false;
        int realSlot = getSlotIndex(slot);
        if (realSlot == -1) return false;
        return inventory.isItemValidForSlot(realSlot, stack);
    }
}
