package ruiseki.okcore.helper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import ruiseki.okcore.datastructure.BlockPos;

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
        String blockName = Block.blockRegistry.getNameForObject(blockState.getBlock())
            .toString();
        int meta = blockState.getPropertyValue("meta");
        return Pair.of(blockName, meta);
    }

    /**
     * Convert the given serialized blockstate to a blockstate instance.
     *
     * @param serializedBlockState The pair of the blockname and meta value.
     * @return The resulting blockstate. Can be null if the referred block does not exist.
     */
    public static BlockState deserializeBlockState(Pair<String, Integer> serializedBlockState) {
        Block block = Block.getBlockFromName(serializedBlockState.getLeft());
        int meta = serializedBlockState.getRight() != null ? serializedBlockState.getRight() : 0;
        if (block != null) {
            ItemStack stack = new ItemStack(block, 1, meta);
            return BlockPropertyRegistry.getBlockState(stack);
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
        return BlockPropertyRegistry.getBlockState(stack);
    }

    /**
     * Get the itemstack from the given blockstate
     *
     * @param blockState The blockstate
     * @return The itemstack
     */
    public static ItemStack getItemStackFromBlockState(BlockState blockState) {
        Item item = Item.getItemFromBlock(blockState.getBlock());
        if (item == null) {
            return null;
        }
        int meta = blockState.getPropertyValue("meta");
        return new ItemStack(item, 1, meta);
    }
}
