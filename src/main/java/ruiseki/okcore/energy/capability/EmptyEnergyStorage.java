package ruiseki.okcore.energy.capability;

import cofh.api.energy.IEnergyStorage;

public class EmptyEnergyStorage implements IEnergyStorage {

    public static final EmptyEnergyStorage INSTANCE = new EmptyEnergyStorage();

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }
}
