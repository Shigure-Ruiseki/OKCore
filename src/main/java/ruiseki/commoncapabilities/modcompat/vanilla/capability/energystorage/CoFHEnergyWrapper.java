package ruiseki.commoncapabilities.modcompat.vanilla.capability.energystorage;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;

public class CoFHEnergyWrapper implements IEnergyStorage {

    protected final TileEntity tile;
    protected final ForgeDirection side;

    public CoFHEnergyWrapper(TileEntity tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (tile instanceof IEnergyReceiver receiver) {
            return receiver.receiveEnergy(side, maxReceive, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (tile instanceof IEnergyProvider provider) {
            return provider.extractEnergy(side, maxExtract, simulate);
        }
        return 0;
    }

    @Override
    public int getEnergyStored() {
        if (tile instanceof IEnergyReceiver r) return r.getEnergyStored(side);
        if (tile instanceof IEnergyProvider p) return p.getEnergyStored(side);
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        if (tile instanceof IEnergyReceiver r) return r.getMaxEnergyStored(side);
        if (tile instanceof IEnergyProvider p) return p.getMaxEnergyStored(side);
        return 0;
    }
}
