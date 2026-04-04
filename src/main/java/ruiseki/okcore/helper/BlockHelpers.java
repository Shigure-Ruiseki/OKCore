package ruiseki.okcore.helper;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * Contains helper methods for various block specific things.
 *
 * @author rubensworks
 */
public final class BlockHelpers {

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
        return creativeTab == null || creativeTab == CreativeTabs.tabAllSearch
            || block.getCreativeTabToDisplayOn() == creativeTab;
    }

    public static float getFacingAngle(ForgeDirection dir) {
        return switch (dir) {
            case SOUTH -> 0F;
            case EAST -> (float) (Math.PI / 2F);
            case NORTH -> (float) Math.PI;
            case WEST -> (float) (3F * Math.PI / 2F);
            default -> 0F;
        };
    }

    public static float getFacingAngle(int side) {
        return getFacingAngle(ForgeDirection.getOrientation(side));
    }

    public static ForgeDirection crossProduct(final ForgeDirection forward, final ForgeDirection up) {
        final int west_x = forward.offsetY * up.offsetZ - forward.offsetZ * up.offsetY;
        final int west_y = forward.offsetZ * up.offsetX - forward.offsetX * up.offsetZ;
        final int west_z = forward.offsetX * up.offsetY - forward.offsetY * up.offsetX;

        return switch (west_x + west_y * 2 + west_z * 3) {
            case 1 -> ForgeDirection.EAST;
            case -1 -> ForgeDirection.WEST;
            case 2 -> ForgeDirection.UP;
            case -2 -> ForgeDirection.DOWN;
            case 3 -> ForgeDirection.SOUTH;
            case -3 -> ForgeDirection.NORTH;
            default -> ForgeDirection.UNKNOWN;
        };

    }

}
