package ruiseki.okcore.energy.capability.ok;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.energy.IOKEnergyIO;
import ruiseki.okcore.energy.capability.IEnergyIO;

public class OKEnergyIO implements IEnergyIO {

    private final IOKEnergyIO handler;
    private final ForgeDirection side;

    public OKEnergyIO(IOKEnergyIO handler, ForgeDirection side) {
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
