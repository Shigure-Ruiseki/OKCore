package ruiseki.okcore.energy;

import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyStorage;
import lombok.Getter;
import lombok.Setter;
import ruiseki.okcore.datastructure.LazyOptional;

public class EnergyTransfer {

    @Getter
    protected LazyOptional<IEnergyStorage> sourceCap = LazyOptional.empty();

    @Setter
    protected LazyOptional<IEnergyStorage> sinkCap = LazyOptional.empty();

    @Setter
    protected int maxEnergyPerTransfer = Integer.MAX_VALUE;
    @Setter
    protected int maxTotalTransferred = Integer.MAX_VALUE;

    @Getter
    protected int totalEnergyTransferred = 0;
    @Getter
    protected int prevEnergyTransferred = 0;

    public void source(IEnergyStorage source) {
        this.sourceCap = source != null ? LazyOptional.of(() -> source) : LazyOptional.empty();
    }

    public void source(LazyOptional<IEnergyStorage> source) {
        this.sourceCap = source != null ? source : LazyOptional.empty();
    }

    public void source(Object source, ForgeDirection side) {
        this.sourceCap = EnergyHelpers.getEnergyStorage(source, side);
    }

    public void sink(IEnergyStorage sink) {
        this.sinkCap = sink != null ? LazyOptional.of(() -> sink) : LazyOptional.empty();
    }

    public void sink(LazyOptional<IEnergyStorage> sink) {
        this.sinkCap = sink != null ? sink : LazyOptional.empty();
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sinkCap = EnergyHelpers.getEnergyStorage(sink, side);
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
        if (!sourceCap.isPresent() || !sinkCap.isPresent()) {
            return 0;
        }

        int remainingAllowance = maxTotalTransferred - totalEnergyTransferred;
        if (remainingAllowance <= 0) {
            prevEnergyTransferred = 0;
            return 0;
        }

        int toTransfer = Math.min(maxEnergyPerTransfer, remainingAllowance);

        return sourceCap.map(src -> sinkCap.map(snk -> {
            int simulatedPull = src.extractEnergy(toTransfer, true);
            if (simulatedPull <= 0) {
                prevEnergyTransferred = 0;
                return 0;
            }

            int simulatedAccepted = snk.receiveEnergy(simulatedPull, true);
            if (simulatedAccepted <= 0) {
                prevEnergyTransferred = 0;
                return 0;
            }

            int actualPulled = src.extractEnergy(simulatedAccepted, false);
            int actualAccepted = snk.receiveEnergy(actualPulled, false);

            if (actualAccepted < actualPulled) {
                int leak = actualPulled - actualAccepted;
                src.receiveEnergy(leak, false);
            }

            totalEnergyTransferred += actualAccepted;
            prevEnergyTransferred = actualAccepted;

            return actualAccepted;
        })
            .orElseGet(() -> {
                prevEnergyTransferred = 0;
                return 0;
            }))
            .orElseGet(() -> {
                prevEnergyTransferred = 0;
                return 0;
            });
    }
}
