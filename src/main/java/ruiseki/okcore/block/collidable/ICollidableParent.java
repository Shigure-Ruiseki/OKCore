package ruiseki.okcore.block.collidable;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Interface used to access the parent methods from a {@link ICollidable}.
 *
 * @author rubensworks
 */
public interface ICollidableParent {

    /**
     * Simply forward this call to the super.
     *
     * @param worldIn The world
     * @param x,      y, z The position
     * @return The selected bounding box
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPoolParent(World worldIn, int x, int y, int z);

    /**
     * Simply forward this call to the super.
     *
     * @param pos         The position
     * @param start       The start vector
     * @param end         The end vector
     * @param boundingBox The bounding box to ray trace with.
     * @return The position object holder
     */
    public MovingObjectPosition rayTraceParent(BlockPos pos, Vec3 start, Vec3 end, AxisAlignedBB boundingBox);
}
