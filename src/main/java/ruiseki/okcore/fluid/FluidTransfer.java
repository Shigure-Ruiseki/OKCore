package ruiseki.okcore.fluid;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import lombok.Getter;
import lombok.Setter;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

@SuppressWarnings({ "unused", "UnusedReturnValue" })
public class FluidTransfer {

    protected LazyOptional<IFluidHandler> source = LazyOptional.empty();
    protected LazyOptional<IFluidHandler> sink = LazyOptional.empty();

    @Setter
    protected int maxPerTransfer = Integer.MAX_VALUE;
    @Setter
    protected int maxTotalTransferred = Integer.MAX_VALUE;

    @Getter
    protected int totalFluidTransferred = 0;
    @Getter
    protected int prevFluidTransferred = 0;

    @Setter
    protected FluidStackPredicate filter = FluidStackPredicate.ALL;

    public void source(IFluidHandler source) {
        this.source = source != null ? LazyOptional.of(() -> source) : LazyOptional.empty();
    }

    public void source(LazyOptional<IFluidHandler> source) {
        this.source = source != null ? source : LazyOptional.empty();
    }

    public void source(Object source, ForgeDirection side) {
        this.source = FluidHelpers.getFluidHandler(source, side);
    }

    public void source(TileEntity source, ForgeDirection side) {
        this.source = FluidHelpers.getFluidHandler(source, side);
    }

    public void sink(IFluidHandler sink) {
        this.sink = sink != null ? LazyOptional.of(() -> sink) : LazyOptional.empty();
    }

    public void sink(LazyOptional<IFluidHandler> sink) {
        this.sink = sink != null ? sink : LazyOptional.empty();
    }

    public void sink(Object sink, ForgeDirection side) {
        this.sink = FluidHelpers.getFluidHandler(sink, side);
    }

    public void sink(TileEntity sink, ForgeDirection side) {
        this.sink = FluidHelpers.getFluidHandler(sink, side);
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
        if (!source.isPresent() || !sink.isPresent()) {
            return 0;
        }

        int remainingAllowance = maxTotalTransferred - totalFluidTransferred;
        if (remainingAllowance <= 0) {
            prevFluidTransferred = 0;
            return 0;
        }

        int maxTransfer = Math.min(maxPerTransfer, remainingAllowance);

        return source.map(srcHandler -> sink.map(sinkHandler -> {
            IFluidTankProperties[] infos = srcHandler.getTankProperties();
            if (infos == null) {
                prevFluidTransferred = 0;
                return 0;
            }

            int totalTransferredInThisCall = 0;

            for (IFluidTankProperties info : infos) {
                if (info == null) continue;

                FluidStack contents = info.getContents();
                if (contents == null || contents.amount <= 0 || contents.getFluid() == null) {
                    continue;
                }

                if (filter != null && !filter.test(contents)) {
                    continue;
                }

                int currentMax = maxTransfer - totalTransferredInThisCall;
                if (currentMax <= 0) break;

                int toExtract = Math.min(contents.amount, currentMax);

                FluidStack simulatedPulled = srcHandler.drain(toExtract, false);
                if (simulatedPulled == null || simulatedPulled.amount <= 0) {
                    continue;
                }

                int simulatedAccepted = sinkHandler.fill(simulatedPulled, false);
                if (simulatedAccepted <= 0) {
                    continue;
                }

                int actualTransferAmount = Math.min(simulatedPulled.amount, simulatedAccepted);
                FluidStack actualTransferStack = simulatedPulled.copy();
                actualTransferStack.amount = actualTransferAmount;

                FluidStack realDrained = srcHandler.drain(actualTransferStack, true);
                if (realDrained == null || realDrained.amount <= 0) {
                    continue;
                }

                int insertedAmount = sinkHandler.fill(realDrained, true);

                if (insertedAmount < realDrained.amount) {
                    FluidStack remainder = realDrained.copy();
                    remainder.amount = realDrained.amount - insertedAmount;
                    srcHandler.fill(remainder, true);
                }

                totalTransferredInThisCall += insertedAmount;
            }

            totalFluidTransferred += totalTransferredInThisCall;
            prevFluidTransferred = totalTransferredInThisCall;

            return totalTransferredInThisCall;

        })
            .orElseGet(() -> {
                prevFluidTransferred = 0;
                return 0;
            }))
            .orElseGet(() -> {
                prevFluidTransferred = 0;
                return 0;
            });
    }
}
