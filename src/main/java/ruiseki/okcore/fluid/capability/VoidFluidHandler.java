package ruiseki.okcore.fluid.capability;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

/**
 * VoidFluidHandler là một template fluid handler cho phép bơm chất lỏng vào vô tận mà không bao giờ đầy.
 * Nó không lưu trữ chất lỏng mà sẽ "tiêu hủy" ngay lập tức khi nhận được.
 */
public class VoidFluidHandler implements IFluidHandler, IFluidTank {

    public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

    public VoidFluidHandler() {}

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null) {
            return null;
        }
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid != null;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { getInfo() };
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
        return Integer.MAX_VALUE;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(null, getCapacity());
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }
        return resource.amount;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return null;
    }
}
