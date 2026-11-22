package ruiseki.okcore.item.capability.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.handler.IItemHandler;

public class InventoryAdapter implements IInventory {

    private final IItemHandler handler;

    public InventoryAdapter(IItemHandler handler) {
        this.handler = handler;
    }

    @Override
    public int getSizeInventory() {
        return this.handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return this.handler.getStackInSlot(slotIn);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (count <= 0) return ItemHelpers.EMPTY;
        return this.handler.extractItem(index, count, false);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {

    }

    @Override
    public String getInventoryName() {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        int slots = handler.getSlots();
        return slots > 0 ? handler.getSlotLimit(0) : 64;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return this.handler.isItemValid(index, stack);
    }
}
