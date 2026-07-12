package ruiseki.okcore.fluid.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import ruiseki.okcore.fluid.FluidTankProperties;
import ruiseki.okcore.fluid.IFluidHandler;
import ruiseki.okcore.fluid.IFluidTankProperties;

public class EmptyFluidHandler implements IFluidHandler, IFluidTank {

    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();
    public static final FluidTankInfo EMPTY_TANK_INFO = new FluidTankInfo(null, 0);
    public static final IFluidTankProperties EMPTY_TANK_PROPERTIES = new FluidTankProperties(null, 0, false, false);
    public static final IFluidTankProperties[] EMPTY_TANK_PROPERTIES_ARRAY = new IFluidTankProperties[] {
        EMPTY_TANK_PROPERTIES };

    protected EmptyFluidHandler() {}

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
        return 0;
    }

    @Override
    public FluidTankInfo getInfo() {
        return EMPTY_TANK_INFO;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
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
