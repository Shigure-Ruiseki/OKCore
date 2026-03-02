package ruiseki.okcore.energy;

import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;

public interface IOKEnergyItem extends IEnergyContainerItem {

    int receiveEnergy(ItemStack stack, int amount, boolean simulate);

    int extractEnergy(ItemStack stack, int amount, boolean simulate);

    int getEnergyStored(ItemStack stack);

    void setEnergyStored(ItemStack stack, int amount);

    int getMaxEnergyStored(ItemStack stack);
}
