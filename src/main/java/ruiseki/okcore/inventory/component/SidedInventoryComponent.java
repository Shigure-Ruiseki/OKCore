package ruiseki.okcore.inventory.component;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public abstract class SidedInventoryComponent extends InventoryComponent implements ISidedInventory {

    public SidedInventoryComponent(TileEntity tile) {
        super(tile);
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
