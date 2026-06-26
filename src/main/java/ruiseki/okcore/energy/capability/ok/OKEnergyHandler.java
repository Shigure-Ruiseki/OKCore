package ruiseki.okcore.energy.capability.ok;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.energy.IOKEnergyHandler;
import ruiseki.okcore.energy.capability.IEnergySink;
import ruiseki.okcore.energy.capability.IEnergySource;

public class OKEnergyHandler implements IEnergySink, IEnergySource {

    private final IOKEnergyHandler receiver;
    private final ForgeDirection side;

    public OKEnergyHandler(IOKEnergyHandler receiver, ForgeDirection side) {
        this.receiver = receiver;
        this.side = side;
    }

    @Override
    public int insert(int amount, boolean simulate) {
        return receiver.receiveEnergy(side, amount, simulate);
    }

    @Override
    public int extract(int amount, boolean simulate) {
        return receiver.extractEnergy(side, amount, simulate);
    }

    @Override
    public boolean canConnect() {
        return receiver.canConnectEnergy(side);
    }
}
