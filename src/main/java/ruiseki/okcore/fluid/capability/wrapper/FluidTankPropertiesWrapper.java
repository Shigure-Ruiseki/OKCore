package ruiseki.okcore.fluid.capability.wrapper;

import javax.annotation.Nullable;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.FluidTank;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

/**
 * Basic {@link IFluidTankProperties} wrapper for {@link FluidTank}.
 */
public class FluidTankPropertiesWrapper implements IFluidTankProperties {

    protected final FluidTank tank;

    public FluidTankPropertiesWrapper(FluidTank tank) {
        this.tank = tank;
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        FluidStack contents = tank.getFluid();
        return contents == null ? null : contents.copy();
    }

    @Override
    public int getCapacity() {
        return tank.getCapacity();
    }

    @Override
    public boolean canFill() {
        return tank.canFill();
    }

    @Override
    public boolean canDrain() {
        return tank.canDrain();
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return tank.canFillFluidType(fluidStack);
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return tank.canDrainFluidType(fluidStack);
    }
}
