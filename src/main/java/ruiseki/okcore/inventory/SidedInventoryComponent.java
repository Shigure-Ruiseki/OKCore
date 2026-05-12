package ruiseki.okcore.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import ruiseki.okcore.item.IItemHandler;

public abstract class SidedInventoryComponent extends InventoryComponent implements ISidedInventory {

    public SidedInventoryComponent(TileEntity tile, IItemHandler handler, String name) {
        super(tile, handler, name);
    }

    public SidedInventoryComponent(TileEntity tile, IItemHandler handler) {
        super(tile, handler, null);
    }

    @Override
    public abstract int[] getAccessibleSlotsFromSide(int side);

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }
}
