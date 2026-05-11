package ruiseki.okcore.item.capability.minecraft;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.ints.IntIterators;
import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.IItemStack2IntFunction;
import ruiseki.okcore.item.IItemStackPredicate;
import ruiseki.okcore.item.capability.IItemSource;

public class InventoryItemSource implements IItemSource {

    public final IInventory inv;
    public final ForgeDirection side;

    private int[] allowedSlots;

    public InventoryItemSource(IInventory inv, ForgeDirection side) {
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
    public void resetSource() {
        IItemSource.super.resetSource();
        allowedSlots = null;
    }

    @Override
    public void setAllowedSourceSlots(int[] slots) {
        this.allowedSlots = slots;
    }

    @Override
    public @Nullable ItemStack pull(@Nullable IItemStackPredicate filter, @Nullable IItemStack2IntFunction amount) {
        InventoryIterator iter = sourceIterator();

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
        return new InventoryIterator(inv, side, getSlots(), allowedSlots) {

            @Override
            protected boolean canAccess(ItemStack stack, int slot) {
                return canExtract(stack, slot);
            }

            @Override
            protected int getSlotStackLimit(int slot, ItemStack stack) {
                return InventoryItemSource.this.getSlotStackLimit(slot, stack);
            }
        };
    }

    protected int getSlotStackLimit(int slot, ItemStack stack) {
        int invStackLimit = inv.getInventoryStackLimit();

        int existingMaxStack = stack == null ? 64 : stack.getMaxStackSize();

        if (invStackLimit > 64) {
            return invStackLimit / 64 * existingMaxStack;
        } else {
            return Math.min(invStackLimit, existingMaxStack);
        }
    }
}
