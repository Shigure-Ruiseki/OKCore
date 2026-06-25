package ruiseki.okcore.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.energy.capability.IEnergySink;
import ruiseki.okcore.energy.capability.IEnergySource;

public class CoFHEnergyHandler implements IEnergyStorage, IEnergySink, IEnergySource {

    protected final IEnergyHandler handler;
    protected final ForgeDirection side;

    public CoFHEnergyHandler(IEnergyHandler handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return handler.receiveEnergy(side, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return handler.extractEnergy(side, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return handler.getEnergyStored(side);
    }

    @Override
    public int getMaxEnergyStored() {
        return handler.getMaxEnergyStored(side);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return receiveEnergy(amount, simulate);
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return extractEnergy(amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return handler.canConnectEnergy(side);
    }
}
