package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.FluidTankProperties;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

public class FluidHandlerWrapper implements IFluidHandler {

    protected final net.minecraftforge.fluids.IFluidHandler handler;
    protected final ForgeDirection side;

    public FluidHandlerWrapper(net.minecraftforge.fluids.IFluidHandler handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return FluidTankProperties.convert(handler.getTankInfo(side));
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return handler.fill(side, resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return handler.drain(side, resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return handler.drain(side, maxDrain, doDrain);
    }
}
