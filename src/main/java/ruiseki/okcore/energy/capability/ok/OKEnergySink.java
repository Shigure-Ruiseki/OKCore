package ruiseki.okcore.energy.capability.ok;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.energy.IOKEnergySink;
import ruiseki.okcore.energy.capability.IEnergySink;

public class OKEnergySink implements IEnergySink {

    private final IOKEnergySink receiver;
    private final ForgeDirection side;

    public OKEnergySink(IOKEnergySink receiver, ForgeDirection side) {
        this.receiver = receiver;
        this.side = side;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return receiver.receiveEnergy(side, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return receiver.canConnectEnergy(side);
    }
}
