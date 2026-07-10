package ruiseki.okcore.energy;

import net.minecraftforge.common.util.ForgeDirection;

import lombok.Getter;
import lombok.Setter;
import ruiseki.okcore.energy.capability.IEnergySink;
import ruiseki.okcore.energy.capability.IEnergySource;

public class EnergyTransfer {

    @Getter
    protected IEnergySource source;
    @Getter
    protected IEnergySink sink;

    @Setter
    protected int maxEnergyPerTransfer = Integer.MAX_VALUE;
    @Setter
    protected int maxTotalTransferred = Integer.MAX_VALUE;

    @Getter
    protected int totalEnergyTransferred = 0;
    @Getter
    protected int prevEnergyTransferred = 0;

    // --- Set source ---
    public void source(IEnergySource source) {
        this.source = source;
    }

    public void source(Object source, ForgeDirection side) {
        this.source = EnergyHelpers.getEnergySource(source, side);
    }

    public void sink(IEnergySink sink) {
        this.sink = sink;
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sink = EnergyHelpers.getEnergySink(sink, side);
    }

    public void push(Object self, ForgeDirection side, Object target) {
        source(self, side);
        sink(target, side.getOpposite());
    }

    public void pull(Object self, ForgeDirection side, Object target) {
        source(target, side.getOpposite());
        sink(self, side);
    }

    public int transfer() {
        if (source == null || sink == null) {
            return 0;
        }

        // 1. Simulate extraction
        int toExtract = Math.min(maxEnergyPerTransfer, maxTotalTransferred);
        int simulatedPull = source.extract(toExtract, true);
        if (simulatedPull <= 0) {
            return 0;
        }

        // 2. Simulate insert
        int simulatedAccepted = sink.insert(simulatedPull, true);
        if (simulatedAccepted <= 0) {
            return 0;
        }

        // 3. Do actual extraction & insertion
        int pulled = source.extract(simulatedAccepted, false);
        int accepted = sink.insert(pulled, false);

        totalEnergyTransferred += accepted;
        prevEnergyTransferred = accepted;

        return accepted;
    }
}
