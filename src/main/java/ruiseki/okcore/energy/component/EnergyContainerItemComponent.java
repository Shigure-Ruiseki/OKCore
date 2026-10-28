
package ruiseki.okcore.energy.component;

import net.minecraft.item.ItemStack;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;

public class EnergyContainerItemComponent implements IEnergyContainerItem {

    protected LazyOptional<IEnergyStorage> getHandler(ItemStack container) {
        return CapabilityHelpers.getCapability(container, CapabilityEnergy.ENERGY);
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return getHandler(container).filter(handler -> maxReceive > 0)
            .map(handler -> handler.receiveEnergy(maxReceive, simulate))
            .orElse(0);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return getHandler(container).filter(handler -> maxExtract > 0)
            .map(handler -> handler.extractEnergy(maxExtract, simulate))
            .orElse(0);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return getHandler(container).map(IEnergyStorage::getEnergyStored)
            .orElse(0);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return getHandler(container).map(IEnergyStorage::getMaxEnergyStored)
            .orElse(0);
    }
}
