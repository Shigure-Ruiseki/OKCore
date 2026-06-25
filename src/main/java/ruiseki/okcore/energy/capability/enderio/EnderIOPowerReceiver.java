package ruiseki.okcore.energy.capability.enderio;

import net.minecraftforge.common.util.ForgeDirection;

import crazypants.enderio.power.IInternalPowerReceiver;
import ruiseki.okcore.energy.capability.IEnergySink;

public class EnderIOPowerReceiver implements IEnergySink {

    private final IInternalPowerReceiver receiver;
    private final ForgeDirection facing;

    public EnderIOPowerReceiver(IInternalPowerReceiver receiver, ForgeDirection facing) {
        this.receiver = receiver;
        this.facing = facing;
    }

    private ForgeDirection getTargetSide() {
        if (facing != null && facing != ForgeDirection.UNKNOWN) {
            return facing;
        }
        if (receiver.canConnectEnergy(ForgeDirection.UNKNOWN)
            && receiver.receiveEnergy(ForgeDirection.UNKNOWN, 1, true) > 0) {
            return ForgeDirection.UNKNOWN;
        }
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (receiver.canConnectEnergy(dir) && receiver.receiveEnergy(dir, 1, true) > 0) {
                return dir;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        ForgeDirection target = getTargetSide();
        if (facing == ForgeDirection.UNKNOWN && target == ForgeDirection.UNKNOWN) {
            return 0;
        }
        return receiver.receiveEnergy(target, amount, simulate);
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
