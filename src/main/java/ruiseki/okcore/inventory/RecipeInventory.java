package ruiseki.okcore.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.item.handler.IItemHandlerModifiable;

public class RecipeInventory implements IInventory {

    private final IItemHandlerModifiable inventory;
    private final int start;
    private final int size;

    public RecipeInventory(IItemHandlerModifiable inventory) {
        this(inventory, 0, inventory != null ? inventory.getSlots() : 0);
    }

    public RecipeInventory(IItemHandlerModifiable inventory, int start, int size) {
        this.inventory = inventory;
        this.start = start;
        this.size = size;
    }

    @Override
    public int getSizeInventory() {
        return this.size;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= this.size) return null;
        return this.inventory.getStackInSlot(slot + this.start);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < 0 || index >= this.size) return null;

        ItemStack stack = this.inventory.getStackInSlot(index + this.start);
        if (stack != null) {
            return this.inventory.extractItem(index + this.start, count, false);
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (index < 0 || index >= this.size) return null;

        ItemStack stack = this.getStackInSlot(index);
        if (stack != null) {
            this.setInventorySlotContents(index, null);
            return stack;
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= this.size) return;
        this.inventory.setStackInSlot(index + this.start, stack);
    }

    @Override
    public String getInventoryName() {
        return "RecipeInventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return this.inventory.getSlotLimit(0);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index < 0 || index >= this.size) return false;
        return this.inventory.isItemValid(index + this.start, stack);
    }

    public boolean isEmpty() {
        for (int i = 0; i < this.size; i++) {
            if (this.inventory.getStackInSlot(i + this.start) != null) {
                return false;
            }
        }
        return true;
    }

    public void clearContent() {
        for (int i = 0; i < this.size; i++) {
            this.inventory.setStackInSlot(i + this.start, null);
        }
    }
}
