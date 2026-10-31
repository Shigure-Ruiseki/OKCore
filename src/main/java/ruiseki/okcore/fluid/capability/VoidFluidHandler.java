
package ruiseki.okcore.fluid.capability;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.fluid.handler.IFluidHandler;

/**
 * VoidFluidHandler is a template fluid handler that can be filled indefinitely
 * without ever getting full.
 *
 * It does not store fluid that gets filled into it, but "destroys" it upon
 * receiving it.
 */
public class VoidFluidHandler implements IFluidHandler, IFluidTank {

    public static final VoidFluidHandler INSTANCE = new VoidFluidHandler();

    public VoidFluidHandler() {}

    // New fluid handler API

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        return null;
    }

    @Override
    public int getTankCapacity(int tank) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    // Legacy IFluidTank API

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
        return new FluidTankInfo(this);
    }

    // Fluid operations

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return resource != null ? resource.amount : 0;
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
