package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.FluidTankProperties;
import ruiseki.okcore.fluid.IFluidHandler;
import ruiseki.okcore.fluid.IFluidTankProperties;

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
    public IFluidTankProperties[] getTankProperties() {
        float percentFilled = fluidBlock.getFilledPercentage(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (percentFilled < 0) {
            percentFilled *= -1;
        }
        int amountFilled = Math.round(FluidHelpers.BUCKET_VOLUME * percentFilled);
        FluidStack fluid = amountFilled > 0 ? new FluidStack(fluidBlock.getFluid(), amountFilled) : null;
        return new FluidTankProperties[] { new FluidTankProperties(fluid, FluidHelpers.BUCKET_VOLUME, false, true) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
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
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || !fluidBlock.canDrain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
            return null;
        }
        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
        if (resource.containsFluid(simulatedDrain)) {
            if (doDrain) {
                return fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), true);
            } else {
                return simulatedDrain;
            }
        }

        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0 || !fluidBlock.canDrain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
            return null;
        }
        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
        if (simulatedDrain != null && simulatedDrain.amount <= maxDrain) {
            if (doDrain) {
                return fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), true);
            } else {
                return simulatedDrain;
            }
        }

        return null;
    }
}
