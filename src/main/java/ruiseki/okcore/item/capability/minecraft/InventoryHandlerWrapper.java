package ruiseki.okcore.item.capability.minecraft;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntIterators;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.IInventoryIterator;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.IItemStack2IntFunction;
import ruiseki.okcore.item.IItemStackPredicate;
import ruiseki.okcore.item.InsertionItemStack;
import ruiseki.okcore.item.capability.IItemSink;
import ruiseki.okcore.item.capability.IItemSource;

public class InventoryHandlerWrapper implements IItemHandler, IItemSink, IItemSource {

    private final IInventory inventory;
    private final ForgeDirection side;
    private int[] allowedSinkSlots;
    private int[] allowedSourceSlots;
    private int slotStackLimit = Integer.MAX_VALUE;

    public InventoryHandlerWrapper(IInventory inventory, ForgeDirection side) {
        this.inventory = inventory;
        this.side = side;
    }

    protected int[] getInvSlots() {
        if (this.inventory instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(this.side.ordinal());
        } else {
            return IntIterators.unwrap(IntIterators.fromTo(0, this.inventory.getSizeInventory()));
        }
    }

    protected int getDynamicSlotStackLimit(int slot, ItemStack stack) {
        int invStackLimit = inventory.getInventoryStackLimit();
        int existingMaxStack = stack == null ? 64 : stack.getMaxStackSize();

        int baseLimit;
        if (invStackLimit > 64) {
            baseLimit = invStackLimit / 64 * existingMaxStack;
        } else {
            baseLimit = Math.min(invStackLimit, existingMaxStack);
        }
        return Math.min(baseLimit, slotStackLimit);
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
        return getDynamicSlotStackLimit(slot, null);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null || stack.stackSize <= 0) return null;

        int actualSlot = slot;
        if (inventory instanceof ISidedInventory sided) {
            int[] accessible = sided.getAccessibleSlotsFromSide(side.ordinal());
            if (slot < 0 || slot >= accessible.length) return stack;
            actualSlot = accessible[slot];
        }

        if (!inventory.isItemValidForSlot(actualSlot, stack)) return stack;
        if (inventory instanceof ISidedInventory sided && !sided.canInsertItem(actualSlot, stack, side.ordinal()))
            return stack;

        ItemStack existing = inventory.getStackInSlot(actualSlot);
        int limit = getDynamicSlotStackLimit(actualSlot, stack);

        if (existing == null) {
            int accepted = Math.min(stack.stackSize, limit);
            if (!simulate) {
                ItemStack newStack = stack.copy();
                newStack.stackSize = accepted;
                inventory.setInventorySlotContents(actualSlot, newStack);
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

        int actualSlot = slot;
        if (inventory instanceof ISidedInventory sided) {
            int[] accessible = sided.getAccessibleSlotsFromSide(side.ordinal());
            if (slot < 0 || slot >= accessible.length) return null;
            actualSlot = accessible[slot];
        }

        ItemStack stackInSlot = inventory.getStackInSlot(actualSlot);
        if (stackInSlot == null) return null;

        if (inventory instanceof ISidedInventory sided
            && !sided.canExtractItem(actualSlot, stackInSlot, side.ordinal())) return null;

        int toExtract = Math.min(amount, stackInSlot.stackSize);
        ItemStack extracted = stackInSlot.copy();
        extracted.stackSize = toExtract;

        if (!simulate) {
            stackInSlot.stackSize -= toExtract;
            if (stackInSlot.stackSize <= 0) {
                inventory.setInventorySlotContents(actualSlot, null);
            }
            inventory.markDirty();
        }
        return extracted;
    }

    @Override
    public void resetSink() {
        IItemSink.super.resetSink();
        this.allowedSinkSlots = null;
        this.slotStackLimit = Integer.MAX_VALUE;
    }

    @Override
    public void setAllowedSinkSlots(int @Nullable [] slots) {
        this.allowedSinkSlots = slots;
    }

    @Override
    public void setSlotStackLimit(int limit) {
        this.slotStackLimit = limit;
    }

    @Override
    public int store(IImmutableItemStack stack) {
        if (stack.isEmpty()) return 0;

        IInventoryIterator iter = sinkIterator();
        InsertionItemStack insertion = new InsertionItemStack(stack);

        while (iter.hasNext()) {
            IImmutableItemStack slot = iter.next();
            if (slot == null || slot.isEmpty()) continue;

            insertion.set(iter.insert(insertion, false));
            if (insertion.isEmpty()) return 0;
        }

        iter.rewind();

        while (iter.hasNext()) {
            IImmutableItemStack slot = iter.next();
            if (slot != null && !slot.isEmpty()) continue;

            insertion.set(iter.insert(insertion, false));
            if (insertion.isEmpty()) return 0;
        }

        return insertion.getStackSize();
    }

    @Override
    public @NotNull IInventoryIterator sinkIterator() {
        return new InventoryIterator(inventory, side, getInvSlots(), allowedSinkSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canInsert(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InventoryHandlerWrapper.this.getDynamicSlotStackLimit(slot, stack);
            }
        };
    }

    @Override
    public @Nullable IInventoryIterator simulatedSinkIterator() {
        return new InventoryIterator(inventory, side, getInvSlots(), allowedSinkSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canInsert(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InventoryHandlerWrapper.this.getDynamicSlotStackLimit(slot, stack);
            }

            @Override
            protected void setInventorySlotContents(int slot, ItemStack stack) {}

            @Override
            protected void markDirty() {}

            @Override
            protected boolean canExtract(ItemStack stack, int slot) {
                return false;
            }

            @Override
            public ItemStack extract(int amount, boolean forced) {
                throw new UnsupportedOperationException();
            }

            @Override
            public IImmutableItemStack previous() {
                OKCore.okLog(Level.WARN, "This simulated sink iterator doesn't support backward traversal");
                return null;
            }

            @Override
            public boolean rewind() {
                return false;
            }
        };
    }

    @Override
    public void resetSource() {
        IItemSource.super.resetSource();
        this.allowedSourceSlots = null;
    }

    @Override
    public void setAllowedSourceSlots(int @Nullable [] slots) {
        this.allowedSourceSlots = slots;
    }

    @Override
    public @Nullable ItemStack pull(@Nullable IItemStackPredicate filter, @Nullable IItemStack2IntFunction amount) {
        IInventoryIterator iter = sourceIterator();

        while (iter.hasNext()) {
            IImmutableItemStack slot = iter.next();
            if (slot == null || slot.isEmpty()) continue;

            if (filter == null || filter.test(slot)) {
                int toExtract = amount == null ? slot.getStackSize() : amount.apply(slot);
                if (toExtract > 0) {
                    return iter.extract(toExtract, false);
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull InventoryIterator sourceIterator() {
        return new InventoryIterator(inventory, side, getInvSlots(), allowedSourceSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canExtract(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InventoryHandlerWrapper.this.getDynamicSlotStackLimit(slot, stack);
            }
        };
    }
}
