package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidHandler;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.FluidHelpers;

public class FluidBlockWrapper implements IFluidHandler {

    protected final IFluidBlock fluidBlock;
    protected final World world;
    protected final BlockPos blockPos;

    public FluidBlockWrapper(IFluidBlock fluidBlock, World world, BlockPos blockPos) {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null) {
            return 0;
        }

        if (resource.amount < FluidHelpers.BUCKET_VOLUME) {
            return 0;
        }

        if (resource.getFluid() != fluidBlock.getFluid()) {
            return 0;
        }

        if (doFill) {
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();

            FluidHelpers.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlock(
                x,
                y,
                z,
                fluidBlock.getFluid()
                    .getBlock(),
                0,
                3);
        }

        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null || resource.getFluid() != fluidBlock.getFluid()) {
            return null;
        }
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        if (maxDrain < FluidHelpers.BUCKET_VOLUME) {
            return null;
        }
        return fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return fluid != null && fluid == fluidBlock.getFluid();
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return fluid != null && fluid == fluidBlock.getFluid();
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        float percentFilled = fluidBlock.getFilledPercentage(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (percentFilled < 0) {
            percentFilled *= -1;
        }
        int amountFilled = Math.round(FluidHelpers.BUCKET_VOLUME * percentFilled);
        FluidStack fluid = amountFilled > 0 ? new FluidStack(fluidBlock.getFluid(), amountFilled) : null;
        return new FluidTankInfo[] { new FluidTankInfo(fluid, FluidHelpers.BUCKET_VOLUME) };
    }
}
