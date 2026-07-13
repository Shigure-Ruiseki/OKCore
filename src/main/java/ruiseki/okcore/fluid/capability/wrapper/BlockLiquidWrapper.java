package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.FluidTankProperties;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

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
    public IFluidTankProperties[] getTankProperties() {
        FluidStack containedStack = null;
        Block blockState = blockPos.getBlock(world);
        if (blockState == blockLiquid) {
            containedStack = getStack();
        }
        return new FluidTankProperties[] {
            new FluidTankProperties(containedStack, FluidHelpers.BUCKET_VOLUME, false, true) };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount < FluidHelpers.BUCKET_VOLUME) {
            return 0;
        }

        if (doFill) {
            int x = blockPos.getX();
            int y = blockPos.getY();
            int z = blockPos.getZ();

            FluidHelpers.destroyBlockOnFluidPlacement(world, blockPos);

            Material material = blockLiquid.getMaterial();
            Block targetBlock = (material == Material.lava) ? Blocks.flowing_lava : Blocks.flowing_water;

            world.setBlock(x, y, z, targetBlock, 0, 3);
        }

        return FluidHelpers.BUCKET_VOLUME;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount < FluidHelpers.BUCKET_VOLUME) {
            return null;
        }

        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        if (block == blockLiquid && meta == 0) {
            FluidStack containedStack = getStack();
            if (containedStack != null && containedStack.isFluidEqual(resource)) {
                if (doDrain) {
                    world.setBlockToAir(x, y, z);
                }
                return containedStack;
            }
        }
        return null;
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain < FluidHelpers.BUCKET_VOLUME) {
            return null;
        }

        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        Block block = world.getBlock(x, y, z);

        if (block == blockLiquid) {
            FluidStack containedStack = getStack();
            if (containedStack != null && containedStack.amount <= maxDrain) {
                if (doDrain) {
                    world.setBlockToAir(x, y, z);
                }
                return containedStack;
            }
        }
        return null;
    }

    private FluidStack getStack() {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        Material material = block.getMaterial();
        if (material == Material.water && meta == 0) {
            return new FluidStack(FluidRegistry.WATER, FluidHelpers.BUCKET_VOLUME);
        } else if (material == Material.lava && meta == 0) {
            return new FluidStack(FluidRegistry.LAVA, FluidHelpers.BUCKET_VOLUME);
        }

        return null;
    }
}
