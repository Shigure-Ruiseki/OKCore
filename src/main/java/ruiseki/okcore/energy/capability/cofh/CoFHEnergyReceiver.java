package ruiseki.okcore.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyReceiver;
import ruiseki.okcore.energy.capability.IEnergySink;

public class CoFHEnergyReceiver implements IEnergySink {

    private final IEnergyReceiver handler;
    private final ForgeDirection side;

    public CoFHEnergyReceiver(IEnergyReceiver handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public boolean canConnect() {
        return handler.canConnectEnergy(side);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!canConnect()) return 0;
        return handler.receiveEnergy(side, amount, simulate);
    }
}
