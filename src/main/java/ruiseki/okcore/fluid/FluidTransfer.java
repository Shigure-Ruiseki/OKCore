package ruiseki.okcore.fluid;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import lombok.Getter;
import lombok.Setter;
import ruiseki.okcore.fluid.capability.IFluidSink;
import ruiseki.okcore.fluid.capability.IFluidSource;

public class FluidTransfer {

    @Getter
    protected IFluidSource source;
    @Getter
    protected IFluidSink sink;
    @Setter
    protected int maxPerTransfer = Integer.MAX_VALUE;
    @Setter
    protected int maxTotalTransferred = Integer.MAX_VALUE;
    @Getter
    protected int transferredThisTick = 0;

    // --- Set source ---
    public void source(IFluidSource source) {
        this.source = source;
    }

    public void source(Object source, ForgeDirection side) {
        this.source = FluidHelpers.getFluidSource(source, side); //
    }

    public void sink(IFluidSink sink) {
        this.sink = sink;
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sink = FluidHelpers.getFluidSink(sink, side);
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

        int maxTransfer = Math.min(maxPerTransfer, maxTotalTransferred - transferredThisTick);
        if (maxTransfer <= 0) {
            return 0;
        }

        FluidTankInfo[] infos = source.getTankInfo();

        if (infos != null) {
            for (FluidTankInfo info : infos) {
                if (info.fluid == null || info.fluid.amount <= 0 || info.fluid.getFluid() == null) {
                    continue;
                }

                FluidStack request = info.fluid.copy();
                request.amount = maxTransfer;

                FluidStack simulatedPulledStack = source.extract(request, false);
                if (simulatedPulledStack == null || simulatedPulledStack.amount <= 0) {
                    continue;
                }

                int pullAmount = simulatedPulledStack.amount;
                FluidStack stackToInsert = simulatedPulledStack.copy();
                stackToInsert.amount = pullAmount;
                int simulatedAccepted = sink.insert(stackToInsert, false);
                if (simulatedAccepted <= 0) {
                    continue;
                }

                int actualTransferAmount = Math.min(pullAmount, simulatedAccepted);

                FluidStack actualTransferStack = simulatedPulledStack.copy();
                actualTransferStack.amount = actualTransferAmount;

                source.extract(actualTransferStack, true);
                int insertedAmount = sink.insert(actualTransferStack, true);

                transferredThisTick += insertedAmount;

                return insertedAmount;
            }
        }

        return 0;
    }
}
