package ruiseki.okcore.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;
import ruiseki.okcore.persist.nbt.INBTSerializable;

/**
 * A TileEntity with an internal inventory based on {@link IItemHandlerModifiable}.
 * Fully integrated with CapabilityCache and Vanilla ISidedInventory support.
 *
 * @author rubensworks
 */
public abstract class InventoryTileEntityBase extends TileEntityOK implements ISidedInventory {

    protected boolean sendUpdateOnInventoryChanged = false;

    public InventoryTileEntityBase() {
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(CapabilityItemHandler.ITEM_HANDLER, this::getItemHandler));
    }

    /**
     * Get the internal item handler.
     *
     * @return The inventory handler instance.
     */
    @NotNull
    public abstract IItemHandlerModifiable getItemHandler();

    protected IInventory getInventory() {
        return this;
    }

    public abstract int[] getSlotsForFace(ForgeDirection side);

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return getSlotsForFace(ForgeDirection.getOrientation(side));
    }

    @Override
    public int getSizeInventory() {
        IItemHandlerModifiable inv = getItemHandler();
        return inv != null ? inv.getSlots() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        IItemHandlerModifiable inv = getItemHandler();
        if (inv == null || slotId < 0 || slotId >= inv.getSlots()) {
            return null;
        }
        return inv.getStackInSlot(slotId);
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        IItemHandlerModifiable inv = getItemHandler();
        if (inv == null || slotId < 0 || slotId >= inv.getSlots()) {
            return null;
        }

        ItemStack extracted = inv.extractItem(slotId, count, false);
        if (extracted != null) {
            onInventoryChanged();
        }
        return extracted;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slotId) {
        IItemHandlerModifiable inv = getItemHandler();
        if (inv == null || slotId < 0 || slotId >= inv.getSlots()) {
            return null;
        }

        ItemStack stack = inv.getStackInSlot(slotId);
        if (stack != null) {
            inv.setStackInSlot(slotId, null);
            onInventoryChanged();
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        IItemHandlerModifiable inv = getItemHandler();
        if (slotId < 0 || slotId >= inv.getSlots()) {
            return;
        }
        inv.setStackInSlot(slotId, itemstack);
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
        return "container.inventory";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        IItemHandlerModifiable inv = getItemHandler();
        return inv.getSlotLimit(0);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityPlayer) {
        return canInteractWith(entityPlayer);
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        IItemHandlerModifiable inv = getItemHandler();
        return inv.isItemValid(index, stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        IItemHandlerModifiable inventory = getItemHandler();
        if (inventory instanceof INBTSerializable) {
            ((INBTSerializable) inventory).deserializeNBT(tag.getCompoundTag("Inventory"));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        IItemHandlerModifiable inventory = getItemHandler();
        if (inventory instanceof INBTSerializable) {
            NBTTagCompound invTag = ((INBTSerializable) inventory).serializeNBT();
            if (invTag != null) {
                tag.setTag("Inventory", invTag);
            }
        }
    }

    protected boolean canAccess(int slot, ForgeDirection side) {
        int[] slots = getAccessibleSlotsFromSide(side.ordinal());
        if (slots == null) return false;
        for (int slotAccess : slots) {
            if (slotAccess == slot) return true;
        }
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        if (!canAccess(slot, ForgeDirection.getOrientation(side))) {
            return false;
        }
        IItemHandlerModifiable inv = getItemHandler();
        ItemStack remainder = inv.insertItem(slot, itemStack, true);
        return remainder == null || remainder.stackSize < itemStack.stackSize;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        if (!canAccess(slot, ForgeDirection.getOrientation(side))) {
            return false;
        }
        IItemHandlerModifiable inv = getItemHandler();
        ItemStack extracted = inv.extractItem(slot, itemStack.stackSize, true);
        return extracted != null && extracted.stackSize > 0;
    }

    public boolean isSendUpdateOnInventoryChanged() {
        return sendUpdateOnInventoryChanged;
    }

    public void setSendUpdateOnInventoryChanged(boolean sendUpdateOnInventoryChanged) {
        this.sendUpdateOnInventoryChanged = sendUpdateOnInventoryChanged;
    }
}
