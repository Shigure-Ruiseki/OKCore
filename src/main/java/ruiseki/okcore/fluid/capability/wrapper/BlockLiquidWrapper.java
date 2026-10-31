
package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;

public class BlockLiquidWrapper implements IFluidHandler {

    protected final BlockLiquid blockLiquid;
    protected final World world;
    protected final BlockPos blockPos;

    public BlockLiquidWrapper(BlockLiquid blockLiquid, World world, BlockPos blockPos) {
        this.blockLiquid = blockLiquid;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank != 0 || !isSourceBlock()) {
            return FluidHelpers.EMPTY;
        }

        FluidStack stack = getStack();
        return stack == null ? FluidHelpers.EMPTY : stack;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tank == 0 ? FluidHelpers.BUCKET_VOLUME : 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        if (tank != 0 || FluidHelpers.isEmpty(stack)) {
            return false;
        }
        FluidStack accepted = getAcceptedFluid();
        return accepted != null && accepted.isFluidEqual(stack);
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (FluidHelpers.isEmpty(resource) || resource.amount < FluidHelpers.BUCKET_VOLUME
            || !isFluidValid(0, resource)) {
            return 0;
        }
        if (doFill) {
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();
            FluidHelpers.destroyBlockOnFluidPlacement(world, blockPos);
            Block targetBlock = blockLiquid.getMaterial() == Material.lava ? Blocks.flowing_lava : Blocks.flowing_water;
            world.setBlock(x, y, z, targetBlock, 0, 3);
        }
        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (FluidHelpers.isEmpty(resource) || resource.amount < FluidHelpers.BUCKET_VOLUME || !isSourceBlock()) {
            return FluidHelpers.EMPTY;
        }
        FluidStack contained = getStack();
        if (contained == null || !contained.isFluidEqual(resource)) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            world.setBlockToAir(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        return contained;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain < FluidHelpers.BUCKET_VOLUME || !isSourceBlock()) {
            return FluidHelpers.EMPTY;
        }
        FluidStack contained = getStack();
        if (contained == null || contained.amount > maxDrain) {
            return FluidHelpers.EMPTY;
        }
        if (doDrain) {
            world.setBlockToAir(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
        return contained;
    }

    private boolean isSourceBlock() {
        return blockPos.getBlock(world) == blockLiquid
            && world.getBlockMetadata(blockPos.getX(), blockPos.getY(), blockPos.getZ()) == 0;
    }

    private FluidStack getAcceptedFluid() {
        Material material = blockLiquid.getMaterial();
        if (material == Material.water) {
            return new FluidStack(FluidRegistry.WATER, 1);
        }
        if (material == Material.lava) {
            return new FluidStack(FluidRegistry.LAVA, 1);
        }
        return null;
    }

    private FluidStack getStack() {
        if (!isSourceBlock()) return null;
        Material material = blockLiquid.getMaterial();
        if (material == Material.water) {
            return new FluidStack(FluidRegistry.WATER, FluidHelpers.BUCKET_VOLUME);
        }
        if (material == Material.lava) {
            return new FluidStack(FluidRegistry.LAVA, FluidHelpers.BUCKET_VOLUME);
        }
        return null;
    }
}
