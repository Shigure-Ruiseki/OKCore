package ruiseki.okcore.helper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.commoncapabilities.api.capability.block.BlockCapabilities;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * Contains helper methods for various block specific things.
 *
 * @author rubensworks
 */
public class BlockHelpers {

    private BlockHelpers() {}

    /**
     * Trigger a block update.
     *
     * @param world The world.
     * @param pos   The pos.
     */
    public static void markForUpdate(World world, BlockPos pos) {
        if (world == null || pos == null) return;
        world.markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Add a collision box to the given list if it intersects with a box.
     *
     * @param pos          The block position the collision is happening in.
     * @param collidingBox The box that is colliding with the block, absolute coordinates.
     * @param collisions   The list fo add the box to.
     * @param addingBox    The box to add to the lost, relative coordinates.
     */
    public static void addCollisionBoxToList(BlockPos pos, AxisAlignedBB collidingBox, List<AxisAlignedBB> collisions,
        AxisAlignedBB addingBox) {
        if (addingBox != null) {
            AxisAlignedBB axisalignedbb = addingBox.offset(pos.getX(), pos.getY(), pos.getZ());
            if (collidingBox.intersectsWith(axisalignedbb)) {
                collisions.add(axisalignedbb);
            }
        }
    }

    /**
     * If the given block has a solid top surface.
     *
     * @param world    The world.
     * @param blockPos The block to check the top of.
     * @return If it has a solid top surface.
     */
    public static boolean doesBlockHaveSolidTopSurface(IBlockAccess world, BlockPos blockPos) {
        return blockPos.getBlock(world)
            .isOpaqueCube();
    }

    /**
     * If the given block can be displayed in the given creative tab.
     *
     * @param block       The block.
     * @param creativeTab The creative tab.
     * @return If it can be displayed.
     */
    public static boolean isValidCreativeTab(Block block, @Nullable CreativeTabs creativeTab) {
        return creativeTab == null || block.getCreativeTabToDisplayOn() == creativeTab;
    }

    /**
     * Convert the given blockstate to a pair of blockname string and meta value.
     *
     * @param blockState The blockstate to serialize.
     * @return The pair of the blockname and meta value.
     */
    public static Pair<String, Integer> serializeBlockState(BlockState blockState) {
        String blockName = Block.blockRegistry.getNameForObject(blockState.getBlock());
        int meta = blockState.getBlockMeta(0);
        return Pair.of(blockName, meta);
    }

    /**
     * Convert the given serialized blockstate to a blockstate instance.
     *
     * @param serializedBlockState The pair of the blockname and meta value.
     * @return The resulting blockstate. Can be null if the referred block does not exist.
     */
    public static BlockState deserializeBlockState(Pair<String, Integer> serializedBlockState) {
        if (serializedBlockState == null || serializedBlockState.getLeft() == null) return null;
        Block block = Block.getBlockFromName(serializedBlockState.getLeft());
        int meta = serializedBlockState.getRight() != null ? serializedBlockState.getRight() : 0;
        if (block != null) {
            return BlockStateHelpers.getState(block, meta);
        }
        return null;
    }

    /**
     * Get the blockstate from the given stack
     *
     * @param stack The stack
     * @return The blockstate
     */
    public static BlockState getBlockStateFromItemStack(ItemStack stack) {
        return BlockStateHelpers.getState(stack);
    }

    /**
     * Get the itemstack from the given blockstate
     *
     * @param blockState The blockstate
     * @return The itemstack
     */
    public static ItemStack getItemStackFromBlockState(BlockState blockState) {
        Item item = Item.getItemFromBlock(blockState.getBlock());
        if (item == null) return null;
        int meta = blockState.getBlockMeta(0);
        return new ItemStack(item, 1, meta);
    }

    /**
     * Safely get a capability from a block.
     * 
     * @param dimPos     The dimensional position of the block.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> LazyOptional<C> getCapability(DimPos dimPos, Capability<C> capability) {
        World world = dimPos.getWorld();
        return getCapability(world, dimPos.getBlockPos(), capability, null);
    }

    /**
     * Safely get a capability from a block.
     * 
     * @param dimPos     The dimensional position of the block.
     * @param capability The capability.
     * @param side       The side to get the capability from.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> LazyOptional<C> getCapability(DimPos dimPos, Capability<C> capability, ForgeDirection side) {
        World world = dimPos.getWorld();
        if (world == null) {
            return null;
        }
        return getCapability(world, dimPos.getBlockPos(), capability, side);
    }

    /**
     * Safely get a capability from a block.
     * 
     * @param world      The world.
     * @param pos        The position of the block providing the capability.
     * @param side       The side to get the capability from.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> LazyOptional<C> getCapability(World world, BlockPos pos, Capability<C> capability,
        ForgeDirection side) {
        return getCapability((IBlockAccess) world, pos, capability, side);
    }

    /**
     * Safely get a capability from a block.
     * 
     * @param world      The world.
     * @param pos        The position of the block providing the capability.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> LazyOptional<C> getCapability(IBlockAccess world, BlockPos pos, Capability<C> capability) {
        return getCapability(world, pos, capability, null);
    }

    /**
     * Safely get a capability from a block.
     * 
     * @param world      The world.
     * @param pos        The position of the block providing the capability.
     * @param side       The side to get the capability from.
     * @param capability The capability.
     * @param <C>        The capability instance.
     * @return The capability or null.
     */
    public static <C> LazyOptional<C> getCapability(IBlockAccess world, BlockPos pos, Capability<C> capability,
        ForgeDirection side) {
        BlockState blockState = BlockStateHelpers.getState(world, pos);
        return BlockCapabilities.getInstance()
            .getCapability(blockState, capability, world, pos, side);
    }
}
