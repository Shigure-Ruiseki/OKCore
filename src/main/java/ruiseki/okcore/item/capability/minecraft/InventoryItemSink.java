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
import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.InsertionItemStack;
import ruiseki.okcore.item.capability.IItemSink;

public class InventoryItemSink implements IItemSink {

    public final IInventory inv;
    private final ForgeDirection side;

    private int[] allowedSlots;
    private int slotStackLimit = Integer.MAX_VALUE;

    public InventoryItemSink(IInventory inv, ForgeDirection side) {
        this.inv = inv;
        this.side = side;
    }

    protected int[] getSlots() {
        if (this.inv instanceof ISidedInventory sided) {
            return sided.getAccessibleSlotsFromSide(this.side.ordinal());
        } else {
            return IntIterators.unwrap(IntIterators.fromTo(0, this.inv.getSizeInventory()));
        }
    }

    @Override
    public void resetSink() {
        IItemSink.super.resetSink();
        allowedSlots = null;
        slotStackLimit = Integer.MAX_VALUE;
    }

    @Override
    public void setAllowedSinkSlots(int @Nullable [] slots) {
        this.allowedSlots = slots;
    }

    @Override
    public void setSlotStackLimit(int limit) {
        this.slotStackLimit = limit;
    }

    @Override
    public int store(IImmutableItemStack stack) {
        if (stack.isEmpty()) return 0;

        InventoryIterator iter = sinkIterator();

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
    public @NotNull InventoryIterator sinkIterator() {
        return new InventoryIterator(inv, side, getSlots(), allowedSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canInsert(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InventoryItemSink.this.getSlotStackLimit(slot, stack);
            }
        };
    }

    protected int getSlotStackLimit(int slot, ItemStack stack) {
        int invStackLimit = inv.getInventoryStackLimit();

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
    public @Nullable InventoryIterator simulatedSinkIterator() {
        return new InventoryIterator(inv, side, getSlots(), allowedSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canInsert(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InventoryItemSink.this.getSlotStackLimit(slot, stack);
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
}
