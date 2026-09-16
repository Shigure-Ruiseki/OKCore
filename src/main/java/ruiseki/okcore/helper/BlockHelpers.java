package ruiseki.okcore.helper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
     * Convert the given blockstate to an NBT tag compound containing the block name and meta value.
     *
     * @param blockState The blockstate to serialize.
     * @return The NBT tag compound containing the block name and meta value.
     */
    public static NBTTagCompound serializeBlockState(BlockState blockState) {
        if (blockState == null || blockState.getBlock() == null) {
            return new NBTTagCompound();
        }
        String blockName = Block.blockRegistry.getNameForObject(blockState.getBlock());
        int meta = blockState.getBlockMeta(0);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("name", blockName != null ? blockName : "");
        tag.setInteger("meta", meta);
        return tag;
    }

    /**
     * Convert the given serialized blockstate NBT tag compound to a blockstate instance.
     *
     * @param serializedBlockState The NBT tag compound containing the block name and meta value.
     * @return The resulting blockstate. Can be null if the referred block does not exist or input is invalid.
     */
    public static BlockState deserializeBlockState(NBTTagCompound serializedBlockState) {
        if (serializedBlockState == null || !serializedBlockState.hasKey("name")) {
            return null;
        }

        String blockName = serializedBlockState.getString("name");
        int meta = serializedBlockState.getInteger("meta");

        Block block = Block.getBlockFromName(blockName);
        if (block == null || block == Blocks.air) {
            return null;
        }

        return BlockStateHelpers.getState(block, meta);
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
        return blockState.getItemStack();
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
