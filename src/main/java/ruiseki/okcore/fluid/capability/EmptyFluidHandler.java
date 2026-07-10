package ruiseki.okcore.fluid.capability;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class EmptyFluidHandler implements IFluidHandler, IFluidTank {

    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();
    public static final FluidTankInfo EMPTY_TANK_INFO = new FluidTankInfo(null, 0);

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { EMPTY_TANK_INFO };
    }

    @Override
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
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }
}
