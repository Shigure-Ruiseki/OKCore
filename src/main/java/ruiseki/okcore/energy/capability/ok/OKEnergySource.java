package ruiseki.okcore.energy.capability.ok;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.energy.IOKEnergySource;
import ruiseki.okcore.energy.capability.IEnergySource;

public class OKEnergySource implements IEnergySource {

    private final IOKEnergySource provider;
    private final ForgeDirection side;

    public OKEnergySource(IOKEnergySource provider, ForgeDirection side) {
        this.provider = provider;
        this.side = side;
    }

    @Override
    public int extract(int amount, boolean simulate) {
        if (!canConnect()) {
            return 0;
        }
        return provider.extractEnergy(side, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return provider.canConnectEnergy(side);
    }
}
