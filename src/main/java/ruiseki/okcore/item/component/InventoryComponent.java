package ruiseki.okcore.item.component;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

public class InventoryComponent implements IInventory {

    @NotNull
    private final TileEntity tile;

    public InventoryComponent(@NotNull TileEntity tile) {
        this.tile = tile;
    }

    protected IItemHandler getHandler() {
        return CapabilityHelpers.getCapability(tile, CapabilityItemHandler.ITEM_HANDLER, ForgeDirection.UNKNOWN)
            .getOrNull();
    }

    @Override
    public int getSizeInventory() {
        IItemHandler handler = getHandler();
        return handler != null ? handler.getSlots() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        IItemHandler handler = getHandler();
        return handler != null ? handler.getStackInSlot(slotIn) : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        IItemHandler handler = getHandler();
        if (handler == null) return null;

        ItemStack result = handler.extractItem(index, count, false);
        if (result != null) this.markDirty();

        return result;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        IItemHandler handler = getHandler();
        if (handler instanceof IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(index, stack);
        }

        this.markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return player.getDistanceSq(tile.xCoord + 0.5D, tile.yCoord + 0.5D, tile.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        IItemHandler handler = getHandler();
        return handler != null && handler.isItemValid(index, stack);
    }
}
