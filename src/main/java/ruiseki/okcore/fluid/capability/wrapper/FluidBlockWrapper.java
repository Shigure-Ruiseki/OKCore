
package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;

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
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0) {
            return FluidHelpers.EMPTY;
        }
        float percentFilled = fluidBlock.getFilledPercentage(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (percentFilled < 0) {
            percentFilled = -percentFilled;
        }
        int amountFilled = Math.round(FluidHelpers.BUCKET_VOLUME * percentFilled);
        if (amountFilled <= 0) {
            return FluidHelpers.EMPTY;
        }
        return new FluidStack(fluidBlock.getFluid(), amountFilled);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? FluidHelpers.BUCKET_VOLUME : 0;
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return tank == 0 && !FluidHelpers.isEmpty(stack) && stack.getFluid() == fluidBlock.getFluid();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (FluidHelpers.isEmpty(resource) || resource.amount < FluidHelpers.BUCKET_VOLUME
            || !isFluidValid(0, resource)) {
            return 0;
        }
        if (doFill) {
            FluidHelpers.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlock(
                blockPos.getX(),
                blockPos.getY(),
                blockPos.getZ(),
                fluidBlock.getFluid()
                    .getBlock(),
                0,
                3);
        }
        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (FluidHelpers.isEmpty(resource)
            || !fluidBlock.canDrain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
            return FluidHelpers.EMPTY;
        }
        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
        if (FluidHelpers.isEmpty(simulatedDrain) || !resource.isFluidEqual(simulatedDrain)
            || resource.amount < simulatedDrain.amount) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            return fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), true);
        }
        return simulatedDrain;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0 || !fluidBlock.canDrain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
            return FluidHelpers.EMPTY;
        }
        FluidStack simulatedDrain = fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), false);
        if (FluidHelpers.isEmpty(simulatedDrain) || simulatedDrain.amount > maxDrain) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            return fluidBlock.drain(world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), true);
        }
        return simulatedDrain;
    }
}
