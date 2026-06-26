package ruiseki.okcore.energy.capability.enderio;

import net.minecraftforge.common.util.ForgeDirection;

import crazypants.enderio.power.IInternalPowerProvider;
import ruiseki.okcore.energy.capability.IEnergySource;

public class EnderIOPowerProvider implements IEnergySource {

    private final IInternalPowerProvider receiver;
    private final ForgeDirection facing;

    public EnderIOPowerProvider(IInternalPowerProvider receiver, ForgeDirection facing) {
        this.receiver = receiver;
        this.facing = facing;
    }

    private ForgeDirection getTargetSide() {
        if (facing != null && facing != ForgeDirection.UNKNOWN) {
            return facing;
        }
        if (receiver.canConnectEnergy(ForgeDirection.UNKNOWN)
            && receiver.extractEnergy(ForgeDirection.UNKNOWN, 1, true) > 0) {
            return ForgeDirection.UNKNOWN;
        }
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (receiver.canConnectEnergy(dir) && receiver.extractEnergy(dir, 1, true) > 0) {
                return dir;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        ForgeDirection target = getTargetSide();
        if (facing == ForgeDirection.UNKNOWN && target == ForgeDirection.UNKNOWN) {
            return 0;
        }
        return receiver.extractEnergy(target, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        ForgeDirection side = facing == null ? ForgeDirection.UNKNOWN : facing;
        if (side == ForgeDirection.UNKNOWN) {
            return getTargetSide() != ForgeDirection.UNKNOWN;
        }
        return receiver.canConnectEnergy(side);
    }
}
