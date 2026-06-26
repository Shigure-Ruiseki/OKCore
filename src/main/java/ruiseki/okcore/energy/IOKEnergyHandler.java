package ruiseki.okcore.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;

/**
 * Interface for tile entities that can both receive and provide energy.
 */
public interface IOKEnergyHandler extends IEnergyHandler, IOKEnergySink, IOKEnergySource {

    @Override
    int receiveEnergy(ForgeDirection side, int amount, boolean simulate);

    @Override
    int extractEnergy(ForgeDirection side, int amount, boolean simulate);

    @Override
    default int getEnergyStored(ForgeDirection forgeDirection) {
        return getEnergyStored();
    }

    @Override
    default int getMaxEnergyStored(ForgeDirection forgeDirection) {
        return getMaxEnergyStored();
    }
}
