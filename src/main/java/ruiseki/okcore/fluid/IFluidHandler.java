package ruiseki.okcore.fluid;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

/**
 * Implement this interface as a capability which should handle fluids, generally storing them in
 * one or more internal {@link IFluidTank} objects.
 */
public interface IFluidHandler extends net.minecraftforge.fluids.IFluidHandler {

    /**
     * Returns an array of objects which represent the internal tanks.
     * These objects cannot be used to manipulate the internal tanks.
     *
     * @return Properties for the relevant internal tanks.
     */
    IFluidTankProperties[] getTankProperties();

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param doFill   If false, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    int fill(FluidStack resource, boolean doFill);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param doDrain  If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    FluidStack drain(FluidStack resource, boolean doDrain);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param doDrain  If false, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     *         simulated) drained.
     */
    FluidStack drain(int maxDrain, boolean doDrain);

    // Wrapper

    @Override
    default FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(maxDrain, doDrain);
    }

    @Override
    default FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return drain(resource, doDrain);
    }

    @Override
    default int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }

    @Override
    default FluidTankInfo[] getTankInfo(ForgeDirection from) {
        IFluidTankProperties[] properties = getTankProperties();
        if (properties == null) {
            return new FluidTankInfo[0];
        }

        FluidTankInfo[] infos = new FluidTankInfo[properties.length];
        for (int i = 0; i < properties.length; i++) {
            infos[i] = new FluidTankInfo(properties[i].getContents(), properties[i].getCapacity());
        }
        return infos;
    }

    @Override
    default boolean canFill(ForgeDirection from, Fluid fluid) {
        if (fluid == null) return false;
        IFluidTankProperties[] properties = getTankProperties();
        if (properties == null) return false;

        FluidStack mockStack = new FluidStack(fluid, 1);
        for (IFluidTankProperties prop : properties) {
            if (prop != null && prop.canFill() && prop.canFillFluidType(mockStack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    default boolean canDrain(ForgeDirection from, Fluid fluid) {
        if (fluid == null) return false;
        IFluidTankProperties[] properties = getTankProperties();
        if (properties == null) return false;

        FluidStack mockStack = new FluidStack(fluid, 1);
        for (IFluidTankProperties prop : properties) {
            if (prop != null && prop.canDrain() && prop.canDrainFluidType(mockStack)) {
                return true;
            }
        }
        return false;
    }
}
