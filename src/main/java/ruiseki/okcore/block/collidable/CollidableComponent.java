package ruiseki.okcore.block.collidable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockHelpers;

/**
 * Component for blocks that require complex multi-part collision and ray-trace detection.
 *
 * @param <P> Position type (e.g., ForgeDirection)
 * @param <B> Block type implementing ICollidableParent
 */
@Data
public class CollidableComponent<P, B extends Block & ICollidableParent> implements ICollidable<P> {

    private final B block;
    private final List<IComponent<P, B>> components;
    private final int totalComponents;

    private AxisAlignedBB lastBounds = ImmutableAxisAlignedBB.fromBounds(0, 0, 0, 1, 1, 1);

    public CollidableComponent(B block, List<IComponent<P, B>> components) {
        this.block = block;
        this.components = components;
        int count = 0;
        for (IComponent<P, B> component : components) {
            for (P position : component.getPossiblePositions()) {
                count += component.getBoundsCount(position);
            }
        }
        this.totalComponents = count;
    }

    private void addComponentCollisionBoxesToList(IComponent<P, B> component, World world, BlockPos pos,
        AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity collidingEntity) {
        for (P position : component.getPossiblePositions()) {
            if (component.isActive(getBlock(), world, pos, position)) {
                for (AxisAlignedBB bb : component.getBounds(getBlock(), world, pos, position)) {
                    BlockHelpers.addCollisionBoxToList(pos, axisalignedbb, list, bb);
                }
            }
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list,
        Entity collidingEntity) {
        BlockPos pos = new BlockPos(x, y, z);
        // Add bounding boxes for all active components.
        for (IComponent<P, B> component : components) {
            addComponentCollisionBoxesToList(component, world, pos, mask, list, collidingEntity);
        }
        // Reset block bounds back to full cube
        getBlock().setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        BlockPos pos = new BlockPos(x, y, z);
        RayTraceResult<P> raytraceResult = doRayTrace(world, pos, origin, direction);
        if (raytraceResult == null) {
            return null;
        } else {
            this.lastBounds = raytraceResult.getBoundingBox();
            return raytraceResult.getMovingObjectPosition();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return lastBounds.getOffsetBoundingBox(x, y, z);
    }

    /**
     * Do a ray trace for the current look direction of the player.
     *
     * @param world  The world.
     * @param pos    The block position to perform a ray trace for.
     * @param player The player.
     * @return A holder object with information on the ray tracing.
     */
    @Override
    public RayTraceResult<P> doRayTrace(World world, BlockPos pos, EntityPlayer player) {
        if (player == null) {
            return null;
        }
        double reachDistance;
        if (player instanceof EntityPlayerMP) {
            reachDistance = ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance();
        } else {
            reachDistance = 5;
        }

        double eyeHeight = player.getEyeHeight();
        Vec3 lookVec = player.getLookVec();
        Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + eyeHeight, player.posZ);
        Vec3 direction = origin
            .addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);

        return doRayTrace(world, pos, origin, direction);
    }

    private int doRayTraceComponent(IComponent<P, B> component, int countStart, World world, BlockPos pos, Vec3 origin,
        Vec3 direction, MovingObjectPosition[] hits, AxisAlignedBB[] boxes, List<P> sideHit,
        List<IComponent<P, B>> components) {
        int i = countStart;
        for (P position : component.getPossiblePositions()) {
            if (component.isActive(getBlock(), world, pos, position)) {
                int offset = 0;
                for (AxisAlignedBB bb : component.getBounds(getBlock(), world, pos, position)) {
                    boxes[i + offset] = bb;
                    hits[i + offset] = getBlock().rayTraceParent(pos, origin, direction, bb);
                    sideHit.set(i + offset, position);
                    components.set(i + offset, component);
                    offset++;
                }
            }
            i += component.getBoundsCount(position);
        }
        return i;
    }

    private RayTraceResult<P> doRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction) {
        // Perform a ray trace for all six sides.
        MovingObjectPosition[] hits = new MovingObjectPosition[totalComponents];
        AxisAlignedBB[] boxes = new AxisAlignedBB[totalComponents];
        List<P> sideHit = new ArrayList<>(Collections.nCopies(totalComponents, null));
        List<IComponent<P, B>> componentsOutput = new ArrayList<>(Collections.nCopies(totalComponents, null));

        // Ray trace for all active components.
        int count = 0;
        for (IComponent<P, B> component : components) {
            count = doRayTraceComponent(
                component,
                count,
                world,
                pos,
                origin,
                direction,
                hits,
                boxes,
                sideHit,
                componentsOutput);
        }

        // Find the closest hit
        double minDistance = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        for (int i = 0; i < hits.length; i++) {
            if (hits[i] != null) {
                double d = hits[i].hitVec.squareDistanceTo(origin);
                if (d < minDistance) {
                    minDistance = d;
                    minIndex = i;
                }
            }
        }

        if (minIndex != -1) {
            return new RayTraceResult<P>(
                hits[minIndex],
                boxes[minIndex],
                sideHit.get(minIndex),
                componentsOutput.get(minIndex));
        }
        return null;
    }

    private void setBlockBounds(AxisAlignedBB bounds) {
        getBlock().setBlockBounds(
            (float) bounds.minX,
            (float) bounds.minY,
            (float) bounds.minZ,
            (float) bounds.maxX,
            (float) bounds.maxY,
            (float) bounds.maxZ);
    }
}
