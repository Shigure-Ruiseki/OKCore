package ruiseki.okcore.energy.capability.wrapper.enderio;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import crazypants.enderio.power.IInternalPowerHandler;
import crazypants.enderio.power.IInternalPowerProvider;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.IPowerStorage;

public class EnderIOPowerWrapper implements IEnergyStorage {

    protected final TileEntity tile;
    protected final ForgeDirection side;

    public EnderIOPowerWrapper(TileEntity tile, ForgeDirection side) {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive <= 0) return 0;

        if (tile instanceof IPowerStorage storage) {
            if (!storage.isInputEnabled(side)) return 0;

            long stored = storage.getEnergyStoredL();
            long maxStored = storage.getMaxEnergyStoredL();
            long space = maxStored - stored;
            if (space <= 0) return 0;

            int maxIn = Math.min(storage.getMaxInput(), maxReceive);
            int accepted = (int) Math.min(maxIn, space);

            if (!simulate && accepted > 0) {
                storage.addEnergy(accepted);
            }
            return accepted;
        }

        if (tile instanceof IInternalPowerReceiver receiver) {
            return receiver.receiveEnergy(side, maxReceive, simulate);
        } else if (tile instanceof IInternalPowerHandler handler) {
            return handler.receiveEnergy(side, maxReceive, simulate);
        }

        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract <= 0) return 0;

        if (tile instanceof IPowerStorage storage) {
            if (!storage.isOutputEnabled(side)) return 0;

            long stored = storage.getEnergyStoredL();
            if (stored <= 0) return 0;

            int maxOut = Math.min(storage.getMaxOutput(), maxExtract);
            int extracted = (int) Math.min(maxOut, stored);

            if (!simulate && extracted > 0) {
                storage.addEnergy(-extracted);
            }
            return extracted;
        }

        if (tile instanceof IInternalPowerProvider provider) {
            return provider.extractEnergy(side, maxExtract, simulate);
        } else if (tile instanceof IInternalPowerHandler handler) {
            return handler.extractEnergy(side, maxExtract, simulate);
        }

        return 0;
    }

    @Override
    public int getEnergyStored() {
        if (tile instanceof IPowerStorage storage) {
            return (int) Math.min(Integer.MAX_VALUE, storage.getEnergyStoredL());
        } else if (tile instanceof IInternalPoweredTile eioTile) {
            return eioTile.getEnergyStored();
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        if (tile instanceof IPowerStorage storage) {
            return (int) Math.min(Integer.MAX_VALUE, storage.getMaxEnergyStoredL());
        } else if (tile instanceof IInternalPoweredTile eioTile) {
            return eioTile.getMaxEnergyStored();
        }
        return 0;
    }
}
