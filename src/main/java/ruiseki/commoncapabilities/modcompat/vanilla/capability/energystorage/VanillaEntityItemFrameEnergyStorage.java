package ruiseki.commoncapabilities.modcompat.vanilla.capability.energystorage;

import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import ruiseki.commoncapabilities.modcompat.vanilla.capability.VanillaEntityItemFrameCapabilityDelegator;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.energy.capability.CapabilityEnergy;

/**
 * An energy handler for entity item frames that have an energy handler.
 * 
 * @author rubensworks
 */
public class VanillaEntityItemFrameEnergyStorage extends VanillaEntityItemFrameCapabilityDelegator<IEnergyStorage>
    implements IEnergyStorage {

    public VanillaEntityItemFrameEnergyStorage(EntityItemFrame entity, ForgeDirection side) {
        super(entity, side);
    }

    @Override
    protected Capability<IEnergyStorage> getCapabilityType() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        ItemStack itemStack = getItemStack();
        IEnergyStorage energyStorage = getCapability(itemStack).getOrNull();
        if (energyStorage != null) {
            int ret = energyStorage.receiveEnergy(maxReceive, simulate);
            if (!simulate && ret > 0) {
                updateItemStack(itemStack);
            }
            return ret;
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        ItemStack itemStack = getItemStack();
        IEnergyStorage energyStorage = getCapability(itemStack).getOrNull();
        if (energyStorage != null) {
            int ret = energyStorage.extractEnergy(maxExtract, simulate);
            if (!simulate && ret > 0) {
                updateItemStack(itemStack);
            }
            return ret;
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        IEnergyStorage energyStorage = getCapability().getOrNull();
        if (energyStorage != null) {
            return energyStorage.getEnergyStored();
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        IEnergyStorage energyStorage = getCapability().getOrNull();
        if (energyStorage != null) {
            return energyStorage.getMaxEnergyStored();
        }
        return 0;
    }
}
