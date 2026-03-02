package ruiseki.okcore.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;
import ruiseki.okcore.energy.capability.IEnergyIO;

public class CoFHEnergyHandler implements IEnergyIO {

    private final IEnergyHandler handler;
    private final ForgeDirection side;

    public CoFHEnergyHandler(IEnergyHandler handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return handler.extractEnergy(side, amount, simulate);
    }

    @Override
    public int insert(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return handler.receiveEnergy(side, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return handler.canConnectEnergy(side);
    }
}
