
package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;

public class FluidHandlerWrapper implements IFluidHandler {

    protected final net.minecraftforge.fluids.IFluidHandler handler;
    protected final ForgeDirection side;

    public FluidHandlerWrapper(@NotNull net.minecraftforge.fluids.IFluidHandler handler, ForgeDirection side) {
        this.handler = handler;
        this.side = side;
    }

    private FluidTankInfo[] getTankInfos() {
        FluidTankInfo[] infos = handler.getTankInfo(side);
        return infos != null ? infos : new FluidTankInfo[0];
    }

    private boolean isValidTank(int tank, FluidTankInfo[] infos) {
        return tank >= 0 && tank < infos.length;
    }

    @Override
    public int getTanks() {
        return getTankInfos().length;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        FluidTankInfo[] infos = getTankInfos();
        if (!isValidTank(tank, infos) || infos[tank] == null) {
            return FluidHelpers.EMPTY;
        }
        FluidStack fluid = infos[tank].fluid;
        return FluidHelpers.isEmpty(fluid) ? FluidHelpers.EMPTY : fluid.copy();
    }

    @Override
    public int getTankCapacity(int tank) {
        FluidTankInfo[] infos = getTankInfos();
        if (!isValidTank(tank, infos) || infos[tank] == null) {
            return 0;
        }
        return Math.max(0, infos[tank].capacity);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        FluidTankInfo[] infos = getTankInfos();
        if (!isValidTank(tank, infos) || infos[tank] == null || FluidHelpers.isEmpty(stack)) {
            return false;
        }
        Fluid fluid = stack.getFluid();
        return fluid != null && handler.canFill(side, fluid);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (FluidHelpers.isEmpty(resource)) {
            return 0;
        }
        return handler.fill(side, resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (FluidHelpers.isEmpty(resource)) {
            return FluidHelpers.EMPTY;
        }
        FluidStack result = handler.drain(side, resource, doDrain);
        return FluidHelpers.isEmpty(result) ? FluidHelpers.EMPTY : result;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return FluidHelpers.EMPTY;
        }
        FluidStack result = handler.drain(side, maxDrain, doDrain);
        return FluidHelpers.isEmpty(result) ? FluidHelpers.EMPTY : result;
    }
}
