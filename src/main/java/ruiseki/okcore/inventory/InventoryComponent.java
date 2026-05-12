package ruiseki.okcore.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.IItemHandlerModifiable;

public class InventoryComponent implements IInventory {

    private final TileEntity tile;
    private final String name;
    private final IItemHandler handler;

    public InventoryComponent(TileEntity tile, IItemHandler handler, String name) {
        this.tile = tile;
        this.name = name;
        this.handler = handler;
    }

    public InventoryComponent(TileEntity tile, IItemHandler handler) {
        this(tile, handler, null);
    }

    @Override
    public int getSizeInventory() {
        return handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return handler.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack result = handler.extractItem(index, count, false);
        if (result != null) markDirty();
        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (handler instanceof IItemHandlerModifiable modifiable) modifiable.setStackInSlot(index, stack);

        markDirty();
    }

    @Override
    public String getInventoryName() {
        return this.name;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return this.name != null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        this.tile.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if (tile == null) return true;
        return player.getDistanceSq(tile.xCoord + 0.5D, tile.yCoord + 0.5D, tile.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return handler.isItemValid(index, stack);
    }
}
