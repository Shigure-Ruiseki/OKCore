package ruiseki.okcore.test;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.item.CapabilityItemHandler;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.ItemStackHandler;
import ruiseki.okcore.persist.nbt.INBTSerializable;

public class BackpackProvider implements IItemHandler, ICapabilityProvider, INBTSerializable {

    public final ItemStackHandler inventory;

    public BackpackProvider() {
        inventory = new ItemStackHandler(4);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, ForgeDirection facing) {
        return capability == CapabilityItemHandler.BACKPACK_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
        return capability == CapabilityItemHandler.BACKPACK_CAPABILITY ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        inventory.deserializeNBT(tag);
    }

    @Override
    public int getSlots() {
        return inventory.getSlots();
    }

    @Override
    public @Nullable ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return inventory.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return inventory.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return inventory.getSlotLimit(slot);
    }
}
