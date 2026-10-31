
package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.fluid.handler.IFluidHandler;

public class FluidHandlerWrapper implements IFluidHandler {

    protected final net.minecraftforge.fluids.IFluidHandler handler;
    protected final ForgeDirection side;

    public FluidHandlerWrapper(net.minecraftforge.fluids.IFluidHandler handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    @Override
    public int getTanks() {
        FluidTankInfo[] infos = handler.getTankInfo(side);
        return infos != null ? infos.length : 0;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        FluidTankInfo[] infos = handler.getTankInfo(side);
        if (infos == null || tank < 0 || tank >= infos.length) {
            return null;
        }
        return infos[tank].fluid;
    }

    @Override
    public int getTankCapacity(int tank) {
        FluidTankInfo[] infos = handler.getTankInfo(side);
        if (infos == null || tank < 0 || tank >= infos.length) {
            return 0;
        }
        return infos[tank].capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        FluidTankInfo[] infos = handler.getTankInfo(side);
        if (infos == null || tank < 0 || tank >= infos.length) {
            return false;
        }

        Fluid fluid = stack.getFluid();
        return fluid != null && handler.canFill(side, fluid);
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
