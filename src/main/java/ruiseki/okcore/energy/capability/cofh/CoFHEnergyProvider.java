package ruiseki.okcore.energy.capability.cofh;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyProvider;
import ruiseki.okcore.energy.capability.IEnergySource;

public class CoFHEnergyProvider implements IEnergySource {

    private final IEnergyProvider handler;
    private final ForgeDirection side;

    public CoFHEnergyProvider(IEnergyProvider handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!canConnect()) return 0;
        return handler.extractEnergy(side, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return handler.canConnectEnergy(side);
    }
}
