
package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;

public class FluidBlockWrapper implements IFluidHandler {

    private static final int TANK_COUNT = 1;
    private static final int TANK_INDEX = 0;
    private static final int CAPACITY = FluidHelpers.BUCKET_VOLUME;

    protected final IFluidBlock fluidBlock;
    protected final World world;
    protected final BlockPos blockPos;

    public FluidBlockWrapper(@NotNull IFluidBlock fluidBlock, @NotNull World world, @NotNull BlockPos blockPos) {
        this.fluidBlock = fluidBlock;
        this.world = world;
        this.blockPos = blockPos;
    }

    private int getX() {
        return blockPos.getX();
    }

    private int getY() {
        return blockPos.getY();
    }

    private int getZ() {
        return blockPos.getZ();
    }

    private boolean canDrain() {
        return fluidBlock.canDrain(world, getX(), getY(), getZ());
    }

    @Nullable
    private FluidStack simulateDrain() {
        if (!canDrain()) {
            return FluidHelpers.EMPTY;
        }
        FluidStack result = fluidBlock.drain(world, getX(), getY(), getZ(), false);
        return FluidHelpers.isEmpty(result) ? FluidHelpers.EMPTY : result;
    }

    @Override
    public int getTanks() {
        return TANK_COUNT;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != TANK_INDEX) {
            return FluidHelpers.EMPTY;
        }
        float percentFilled = fluidBlock.getFilledPercentage(world, getX(), getY(), getZ());
        if (Float.isNaN(percentFilled) || Float.isInfinite(percentFilled)) {
            return FluidHelpers.EMPTY;
        }
        percentFilled = Math.min(1.0F, Math.abs(percentFilled));
        int amountFilled = Math.round(CAPACITY * percentFilled);
        if (amountFilled <= 0) {
            return FluidHelpers.EMPTY;
        }
        return new FluidStack(fluidBlock.getFluid(), amountFilled);
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == TANK_INDEX ? CAPACITY : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return tank == TANK_INDEX && !FluidHelpers.isEmpty(stack)
            && fluidBlock.getFluid() != null
            && stack.getFluid() == fluidBlock.getFluid();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (FluidHelpers.isEmpty(resource) || resource.amount < CAPACITY || !isFluidValid(TANK_INDEX, resource)) {
            return 0;
        }
        if (doFill) {
            Block block = fluidBlock.getFluid()
                .getBlock();
            if (block == null) {
                return 0;
            }
            FluidHelpers.destroyBlockOnFluidPlacement(world, blockPos);
            boolean placed = world.setBlock(getX(), getY(), getZ(), block, 0, 3);
            if (!placed) {
                return 0;
            }
        }
        return CAPACITY;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (FluidHelpers.isEmpty(resource) || resource.amount <= 0) {
            return FluidHelpers.EMPTY;
        }
        FluidStack simulatedDrain = simulateDrain();
        if (FluidHelpers.isEmpty(simulatedDrain) || !simulatedDrain.isFluidEqual(resource)) {
            return FluidHelpers.EMPTY;
        }
        int drainAmount = Math.min(resource.amount, simulatedDrain.amount);
        if (drainAmount <= 0) {
            return FluidHelpers.EMPTY;
        }
        /*
         * IFluidBlock.drain() only supports draining the block's
         * available fluid according to its own implementation.
         * If the requested amount is smaller than the available amount,
         * do not execute a full drain and discard the excess.
         */
        if (drainAmount < simulatedDrain.amount) {
            return FluidHelpers.EMPTY;
        }
        if (!doDrain) {
            return simulatedDrain;
        }
        FluidStack drained = fluidBlock.drain(world, getX(), getY(), getZ(), true);
        return FluidHelpers.isEmpty(drained) ? FluidHelpers.EMPTY : drained;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) {
            return FluidHelpers.EMPTY;
        }
        FluidStack simulatedDrain = simulateDrain();
        if (FluidHelpers.isEmpty(simulatedDrain) || simulatedDrain.amount > maxDrain) {
            return FluidHelpers.EMPTY;
        }
        if (!doDrain) {
            return simulatedDrain;
        }
        FluidStack drained = fluidBlock.drain(world, getX(), getY(), getZ(), true);
        return FluidHelpers.isEmpty(drained) ? FluidHelpers.EMPTY : drained;
    }
}
