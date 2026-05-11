package ruiseki.okcore.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;

public class CoFHReceiverWrapper implements IEnergyStorage {

    protected final IEnergyReceiver receiver;
    protected final ForgeDirection side;

    public CoFHReceiverWrapper(IEnergyReceiver receiver, ForgeDirection side) {
        this.receiver = receiver;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return receiver.receiveEnergy(side, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
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
