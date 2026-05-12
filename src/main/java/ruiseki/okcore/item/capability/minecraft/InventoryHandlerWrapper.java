package ruiseki.okcore.item.capability.minecraft;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.item.IItemHandler;

public class InventoryHandlerWrapper implements IItemHandler {

    private final IInventory inventory;
    private final ForgeDirection side;

    public InventoryHandlerWrapper(IInventory inventory, ForgeDirection side) {
        this.inventory = inventory;
        this.side = side;
    }

    @Override
    public int getSlots() {
        if (inventory instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(side.ordinal()).length;
        }
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize <= 0) return null;
        if (!inventory.isItemValidForSlot(slot, stack)) return stack;

        if (inventory instanceof ISidedInventory sided) {
            if (!sided.canInsertItem(slot, stack, side.ordinal())) return stack;
        }

        ItemStack existing = inventory.getStackInSlot(slot);
        int limit = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());

        if (existing == null) {
            int accepted = Math.min(stack.stackSize, limit);
            if (!simulate) {
                ItemStack newStack = stack.copy();
                newStack.stackSize = accepted;
                inventory.setInventorySlotContents(slot, newStack);
                inventory.markDirty();
            }
            if (accepted >= stack.stackSize) return null;
            ItemStack remainder = stack.copy();
            remainder.stackSize -= accepted;
            return remainder;
        } else {
            if (!ItemStackHelpers.areStackMergable(existing, stack)) return stack;

            int space = limit - existing.stackSize;
            if (space <= 0) return stack;

            int accepted = Math.min(stack.stackSize, space);
            if (!simulate) {
                existing.stackSize += accepted;
                inventory.markDirty();
            }

            if (accepted >= stack.stackSize) return null;
            ItemStack remainder = stack.copy();
            remainder.stackSize -= accepted;
            return remainder;
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return null;

        ItemStack stackInSlot = inventory.getStackInSlot(slot);
        if (stackInSlot == null) return null;

        if (inventory instanceof ISidedInventory sided) {
            if (!sided.canExtractItem(slot, stackInSlot, side.ordinal())) return null;
        }

        int toExtract = Math.min(amount, stackInSlot.stackSize);
        ItemStack extracted = stackInSlot.copy();
        extracted.stackSize = toExtract;

        if (!simulate) {
            stackInSlot.stackSize -= toExtract;
            if (stackInSlot.stackSize <= 0) {
                inventory.setInventorySlotContents(slot, null);
            }
            inventory.markDirty();
        }
        return extracted;
    }
}
