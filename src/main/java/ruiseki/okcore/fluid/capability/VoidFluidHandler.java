package ruiseki.okcore.fluid.capability;

import static ruiseki.okcore.fluid.capability.EmptyFluidHandler.EMPTY_TANK_INFO;
import static ruiseki.okcore.fluid.capability.EmptyFluidHandler.EMPTY_TANK_PROPERTIES_ARRAY;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

/**
 * VoidFluidHandler is a template fluid handler that can be filled indefinitely without ever getting full.
 * It does not store fluid that gets filled into it, but "destroys" it upon receiving it.
 */
public class VoidFluidHandler implements IFluidHandler, IFluidTank {

    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

    public VoidFluidHandler() {}

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return EMPTY_TANK_PROPERTIES_ARRAY;
    }

    @Override
    @Nullable
    public FluidStack getFluid() {
        return null;
    }

    @Override
    public int getFluidAmount() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public FluidTankInfo getInfo() {
        return EMPTY_TANK_INFO;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return resource.amount;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }
}
