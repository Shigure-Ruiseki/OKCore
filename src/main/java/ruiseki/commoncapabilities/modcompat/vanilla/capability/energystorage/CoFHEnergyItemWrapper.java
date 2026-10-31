
package ruiseki.commoncapabilities.modcompat.vanilla.capability.energystorage;

import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyStorage;

public class CoFHEnergyItemWrapper implements IEnergyStorage {

    private final ItemStack stack;
    private final IEnergyContainerItem item;

    public CoFHEnergyItemWrapper(ItemStack stack, IEnergyContainerItem item) {
        this.stack = stack;
        this.item = item;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return item.receiveEnergy(stack, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return item.extractEnergy(stack, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return item.getEnergyStored(stack);
    }

    @Override
    public int getMaxEnergyStored() {
        return item.getMaxEnergyStored(stack);
    }
}
