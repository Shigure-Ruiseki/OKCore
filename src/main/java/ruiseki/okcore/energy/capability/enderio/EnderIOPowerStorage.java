package ruiseki.okcore.energy.capability.enderio;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IPowerStorage;
import ruiseki.okcore.energy.capability.IEnergySink;
import ruiseki.okcore.energy.capability.IEnergySource;

public class EnderIOPowerStorage implements IEnergyStorage, IEnergySink, IEnergySource {

    private final IPowerStorage storage;
    private final ForgeDirection facing;

    public EnderIOPowerStorage(IPowerStorage storage, ForgeDirection facing) {
        this.storage = storage;
        this.facing = facing;
    }

    private IPowerStorage getController() {
        IPowerStorage controller = storage.getController();
        return controller != null ? controller : storage;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        IPowerStorage ctrl = getController();
        ForgeDirection side = facing == null ? ForgeDirection.UNKNOWN : facing;

        if (side != ForgeDirection.UNKNOWN && !ctrl.isInputEnabled(side)) {
            return 0;
        }

        if (ctrl instanceof IInternalPowerReceiver receiver) {
            return receiver.receiveEnergy(side, maxReceive, simulate);
        }
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        IPowerStorage ctrl = getController();
        ForgeDirection side = facing == null ? ForgeDirection.UNKNOWN : facing;

        if (side != ForgeDirection.UNKNOWN && !ctrl.isOutputEnabled(side)) {
            return 0;
        }

        long storedL = ctrl.getEnergyStoredL();
        if (storedL <= 0) return 0;

        int maxOutput = ctrl.getMaxOutput();
        long maxExtractL = Math.min(maxExtract, Math.min(storedL, maxOutput));
        int toExtract = (int) maxExtractL;

        if (!simulate && toExtract > 0) {
            ctrl.addEnergy(-toExtract);
            if (storage instanceof TileEntity tile) {
                tile.markDirty();
            }
        }
        return toExtract;
    }

    @Override
    public int getEnergyStored() {
        return (int) Math.min(getController().getEnergyStoredL(), Integer.MAX_VALUE);
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) Math.min(getController().getMaxEnergyStoredL(), Integer.MAX_VALUE);
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
        IPowerStorage ctrl = getController();
        ForgeDirection side = facing == null ? ForgeDirection.UNKNOWN : facing;
        if (side == ForgeDirection.UNKNOWN) return true;
        return ctrl.isInputEnabled(side) || ctrl.isOutputEnabled(side);
    }
}
