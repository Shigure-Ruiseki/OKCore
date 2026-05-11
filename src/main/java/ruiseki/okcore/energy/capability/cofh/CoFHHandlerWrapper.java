package ruiseki.okcore.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;

public class CoFHHandlerWrapper implements IEnergyStorage {

    protected final IEnergyHandler receiver;
    protected final ForgeDirection side;

    public CoFHHandlerWrapper(IEnergyHandler receiver, ForgeDirection side) {
        this.receiver = receiver;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return receiver.receiveEnergy(side, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return receiver.extractEnergy(side, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return receiver.getEnergyStored(side);
    }

    @Override
    public int getMaxEnergyStored() {
        return receiver.getMaxEnergyStored(side);
    }

    @Override
    public boolean canExtract() {
        return receiver.canConnectEnergy(side);
    }

    @Override
    public boolean canReceive() {
        return receiver.canConnectEnergy(side);
    }
}
