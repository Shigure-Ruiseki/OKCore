package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.capability.VoidFluidHandler;

/**
 * Wrapper around any block, only accounts for fluid placement, otherwise the block acts a void.
 * If the block in question inherits from the default Vanilla or Forge implementations,
 * consider using {@link BlockLiquidWrapper} or {@link FluidBlockWrapper} respectively.
 */
public class BlockWrapper extends VoidFluidHandler {

    protected final Block block;
    protected final World world;
    protected final BlockPos blockPos;

    public BlockWrapper(Block block, World world, BlockPos blockPos) {
        this.block = block;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource.amount < FluidHelpers.BUCKET_VOLUME) {
            return 0;
        }
        if (doFill) {
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();
            FluidHelpers.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlock(x, y, z, block, 0, 3);
        }
        return FluidHelpers.BUCKET_VOLUME;
    }
}
