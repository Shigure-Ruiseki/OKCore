package ruiseki.okcore.inventory;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.google.common.collect.Lists;

import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.persist.IDirtyMarkListener;

/**
 * A basic inventory implementation for Minecraft 1.7.10.
 * 
 * @author rubensworks
 *
 */
public class SimpleInventory implements INBTInventory {

    protected final ItemStack[] _contents;
    private final String _name;
    private final int _stackLimit;
    private final List<IDirtyMarkListener> dirtyMarkListeners = Lists.newLinkedList();

    /**
     * Default constructor for NBT persistence, don't call this yourself.
     */
    public SimpleInventory() {
        this(0, "", 0);
    }

    /**
     * Make a new instance.
     * 
     * @param size       The amount of slots in the inventory.
     * @param name       The name of the inventory, used for NBT storage.
     * @param stackLimit The stack limit for each slot.
     */
    public SimpleInventory(int size, String name, int stackLimit) {
        _contents = new ItemStack[size];
        for (int i = 0; i < _contents.length; i++) {
            _contents[i] = null;
        }
        _name = name;
        _stackLimit = stackLimit;
    }

    /**
     * Add a dirty marking listener.
     * 
     * @param dirtyMarkListener The dirty mark listener.
     */
    public synchronized void addDirtyMarkListener(IDirtyMarkListener dirtyMarkListener) {
        this.dirtyMarkListeners.add(dirtyMarkListener);
    }

    /**
     * Remove a dirty marking listener.
     * 
     * @param dirtyMarkListener The dirty mark listener.
     */
    public synchronized void removeDirtyMarkListener(IDirtyMarkListener dirtyMarkListener) {
        this.dirtyMarkListeners.remove(dirtyMarkListener);
    }

    @Override
    public int getSizeInventory() {
        return _contents.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotId) {
        if (slotId < 0 || slotId >= _contents.length) {
            return null;
        }
        return _contents[slotId];
    }

    @Override
    public ItemStack decrStackSize(int slotId, int count) {
        ItemStack stack = getStackInSlot(slotId);
        if (slotId < getSizeInventory() && stack != null) {
            if (stack.stackSize > count) {
                ItemStack slotContents = stack.copy();
                ItemStack result = slotContents.splitStack(count);
                setInventorySlotContents(slotId, slotContents);
                return result;
            }
            setInventorySlotContents(slotId, null);
            onInventoryChanged();
            return stack;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (this._contents[index] != null) {
            ItemStack itemstack = this._contents[index];
            this._contents[index] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        if (slotId < 0 || slotId >= getSizeInventory()) {
            return;
        }
        this._contents[slotId] = itemstack;

        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit()) {
            itemstack.stackSize = this.getInventoryStackLimit();
        }
        onInventoryChanged();
    }

    @Override
    public String getInventoryName() {
        return _name != null ? _name : "";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return _name != null && !_name.isEmpty();
    }

    @Override
    public int getInventoryStackLimit() {
        return _stackLimit;
    }

    protected void onInventoryChanged() {
        markDirty();
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
    public void readFromNBT(NBTTagCompound data) {
        readFromNBT(data, "items");
    }

    /**
     * Read inventory data from the given NBT.
     * 
     * @param data The NBT data containing inventory data.
     * @param tag  The NBT tag name where the info is located.
     */
    public void readFromNBT(NBTTagCompound data, String tag) {
        NBTTagList nbttaglist = data.getTagList(tag, MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());

        for (int j = 0; j < getSizeInventory(); ++j) {
            _contents[j] = null;
        }

        for (int j = 0; j < nbttaglist.tagCount(); ++j) {
            NBTTagCompound slot = nbttaglist.getCompoundTagAt(j);
            int index;
            if (slot.hasKey("index")) {
                index = slot.getInteger("index");
            } else {
                index = slot.getByte("Slot");
            }
            if (index >= 0 && index < getSizeInventory()) {
                _contents[index] = ItemStack.loadItemStackFromNBT(slot);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound data) {
        writeToNBT(data, "items");
    }

    /**
     * Write inventory data to the given NBT.
     * 
     * @param data The NBT tag that will receive inventory data.
     * @param tag  The NBT tag name where the info must be located.
     */
    public void writeToNBT(NBTTagCompound data, String tag) {
        NBTTagList slots = new NBTTagList();
        for (byte index = 0; index < getSizeInventory(); ++index) {
            ItemStack itemStack = getStackInSlot(index);
            if (itemStack != null && itemStack.stackSize > 0) {
                NBTTagCompound slot = new NBTTagCompound();
                slots.appendTag(slot);
                slot.setByte("Slot", index);
                itemStack.writeToNBT(slot);
            }
        }
        data.setTag(tag, slots);
    }

    public ItemStack removeStackFromSlot(int slotId) {
        ItemStack stackToTake = getStackInSlot(slotId);
        if (stackToTake == null) {
            return null;
        }

        setInventorySlotContents(slotId, null);
        return stackToTake;
    }

    /**
     * Get the array of {@link net.minecraft.item.ItemStack} inside this inventory.
     * 
     * @return The items in this inventory.
     */
    public ItemStack[] getItemStacks() {
        return _contents;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return i < getSizeInventory() && i >= 0;
    }

    public void clear() {
        for (int i = 0; i < getSizeInventory(); i++) {
            _contents[i] = null;
        }
        onInventoryChanged();
    }

    @Override
    public void markDirty() {
        List<IDirtyMarkListener> listeners;
        synchronized (this) {
            listeners = Lists.newLinkedList(this.dirtyMarkListeners);
        }
        for (IDirtyMarkListener dirtyMarkListener : listeners) {
            dirtyMarkListener.onDirty();
        }
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        readFromNBT(tag);
    }
}
