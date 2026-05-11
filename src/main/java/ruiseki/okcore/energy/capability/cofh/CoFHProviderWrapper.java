package ruiseki.okcore.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyStorage;

public class CoFHProviderWrapper implements IEnergyStorage {

    protected final IEnergyProvider provider;
    protected final ForgeDirection side;

    public CoFHProviderWrapper(IEnergyProvider provider, ForgeDirection side) {
        this.provider = provider;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return provider.extractEnergy(side, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return provider.getEnergyStored(side);
    }

    @Override
    public int getMaxEnergyStored() {
        return provider.getMaxEnergyStored(side);
    }

    @Override
    public boolean canExtract() {
        return provider.canConnectEnergy(side);
    }

    @Override
    public boolean canReceive() {
        return provider.canConnectEnergy(side);
    }
}
