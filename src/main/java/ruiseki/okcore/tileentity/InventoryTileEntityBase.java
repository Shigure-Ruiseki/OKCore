package ruiseki.okcore.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.inventory.INBTInventory;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.capability.wrapper.InvWrapper;

/**
 * A TileEntity with an internal inventory.
 * Integrated with CapabilityCache support.
 *
 * @author rubensworks
 */
public abstract class InventoryTileEntityBase extends TileEntityOK implements ISidedInventory {

    protected boolean sendUpdateOnInventoryChanged = false;

    public InventoryTileEntityBase() {
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(CapabilityItemHandler.ITEM_HANDLER, () -> new InvWrapper(getInventory())));
    }

    /**
     * Get the internal inventory.
     *
     * @return The inventory instance.
     */
    @NotNull
    public abstract INBTInventory getInventory();

    public abstract int[] getSlotsForFace(ForgeDirection side);

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return getSlotsForFace(ForgeDirection.getOrientation(side));
    }

    @Override
    public int getSizeInventory() {
        INBTInventory inv = getInventory();
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        if (slotId < 0 || slotId >= getSizeInventory()) {
            return null;
        }
        INBTInventory inv = getInventory();
        return inv.getStackInSlot(slotId);
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        INBTInventory inv = getInventory();

        ItemStack itemStack = inv.decrStackSize(slotId, count);
        onInventoryChanged();
        return itemStack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotId) {
        INBTInventory inv = getInventory();
        return inv.getStackInSlotOnClosing(slotId);
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        INBTInventory inv = getInventory();
        inv.setInventorySlotContents(slotId, itemstack);
        onInventoryChanged();
    }

    protected void onInventoryChanged() {
        markDirty();
        if (isSendUpdateOnInventoryChanged()) {
            sendUpdate();
        }
    }

    @Override
    public String getInventoryName() {
        INBTInventory inv = getInventory();
        return inv.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        INBTInventory inv = getInventory();
        return inv.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        INBTInventory inv = getInventory();
        return inv.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return canInteractWith(entityPlayer);
    }

    @Override
    public void openInventory() {
        INBTInventory inv = getInventory();
        inv.openInventory();
    }

    @Override
    public void closeInventory() {
        INBTInventory inv = getInventory();
        inv.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        INBTInventory inv = getInventory();
        return inv.isItemValidForSlot(index, stack);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        INBTInventory inventory = getInventory();
        inventory.readFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        INBTInventory inventory = getInventory();
        inventory.writeToNBT(tag);
    }

    protected boolean canAccess(int slot, ForgeDirection side) {
        int[] slots = getAccessibleSlotsFromSide(side.ordinal());
        for (int slotAccess : slots) {
            if (slotAccess == slot) return true;
        }
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return canAccess(slot, ForgeDirection.getOrientation(side)) && this.isItemValidForSlot(slot, itemStack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        return canAccess(slot, ForgeDirection.getOrientation(side));
    }

    public boolean isSendUpdateOnInventoryChanged() {
        return sendUpdateOnInventoryChanged;
    }

    public void setSendUpdateOnInventoryChanged(boolean sendUpdateOnInventoryChanged) {
        this.sendUpdateOnInventoryChanged = sendUpdateOnInventoryChanged;
    }
}
