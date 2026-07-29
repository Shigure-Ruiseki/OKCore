package ruiseki.okcore.block.collidable;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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

    public CollidableComponent(B block, List<IComponent<P, B>> components) {
        this.block = block;
        this.components = components;
    }

    private void addComponentCollisionBoxesToList(IComponent<P, B> component, World world, int x, int y, int z,
        AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, Entity collidingEntity) {
        for (P position : component.getPossiblePositions()) {
            if (component.isActive(getBlock(), world, x, y, z, position)) {
                List<AxisAlignedBB> boundsList = component.getBounds(getBlock(), world, x, y, z, position);
                if (boundsList == null) continue;

                for (AxisAlignedBB bb : boundsList) {
                    if (bb == null) continue; // Skip null bounding box
                    setBlockBounds(bb);
                    getBlock().addCollisionBoxesToListParent(world, x, y, z, axisalignedbb, list, collidingEntity);
                }
            }
        }
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisalignedbb,
        List<AxisAlignedBB> list, Entity collidingEntity) {
        try {
            if (components != null) {
                for (IComponent<P, B> component : components) {
                    if (component != null) {
                        addComponentCollisionBoxesToList(
                            component,
                            world,
                            x,
                            y,
                            z,
                            axisalignedbb,
                            list,
                            collidingEntity);
                    }
                }
            }
        } finally {
            getBlock().setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return ((ICollidableParent) getBlock()).getSelectedBoundingBoxFromPoolParent(world, x, y, z);
        }

        RayTraceResult<P> rayTraceResult = doRayTrace(world, x, y, z, player);
        if (rayTraceResult != null && rayTraceResult.getBoundingBox() != null) {
            AxisAlignedBB box = rayTraceResult.getBoundingBox();
            return box.offset(x, y, z);
        }

        return ((ICollidableParent) getBlock()).getSelectedBoundingBoxFromPoolParent(world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        RayTraceResult<P> raytraceResult = doRayTrace(world, x, y, z, origin, direction);
        return raytraceResult == null ? null : raytraceResult.getMovingObjectPosition();
    }

    public RayTraceResult<P> doRayTrace(World world, int x, int y, int z, EntityPlayer player) {
        double reachDistance = (player instanceof EntityPlayerMP)
            ? ((EntityPlayerMP) player).theItemInWorldManager.getBlockReachDistance()
            : 5.0D;

        Vec3 lookVec = player.getLookVec();
        Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 direction = origin
            .addVector(lookVec.xCoord * reachDistance, lookVec.yCoord * reachDistance, lookVec.zCoord * reachDistance);

        return doRayTrace(world, x, y, z, origin, direction);
    }

    private RayTraceResult<P> doRayTrace(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        List<RayTraceResult<P>> results = new ArrayList<>();

        try {
            if (components != null) {
                for (IComponent<P, B> component : components) {
                    if (component == null) continue;

                    for (P position : component.getPossiblePositions()) {
                        if (component.isActive(getBlock(), world, x, y, z, position)) {
                            List<AxisAlignedBB> boundsList = component.getBounds(getBlock(), world, x, y, z, position);
                            if (boundsList == null) continue;

                            for (AxisAlignedBB bb : boundsList) {
                                if (bb == null) continue; // Safe null check

                                setBlockBounds(bb);
                                MovingObjectPosition mop = getBlock()
                                    .collisionRayTraceParent(world, x, y, z, origin, direction);
                                if (mop != null) {
                                    results.add(new RayTraceResult<P>(mop, bb, position, component));
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            getBlock().setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
        }

        // Find the closest hit distance from camera origin
        RayTraceResult<P> closestHit = null;
        double minDistance = Double.POSITIVE_INFINITY;

        for (RayTraceResult<P> result : results) {
            if (result.getMovingObjectPosition() != null && result.getMovingObjectPosition().hitVec != null) {
                double dist = result.getMovingObjectPosition().hitVec.squareDistanceTo(origin);
                if (dist < minDistance) {
                    minDistance = dist;
                    closestHit = result;
                }
            }
        }

        return closestHit;
    }

    private void setBlockBounds(AxisAlignedBB bounds) {
        if (bounds == null) {
            getBlock().setBlockBounds(0F, 0F, 0F, 1F, 1F, 1F);
            return;
        }

        getBlock().setBlockBounds(
            (float) bounds.minX,
            (float) bounds.minY,
            (float) bounds.minZ,
            (float) bounds.maxX,
            (float) bounds.maxY,
            (float) bounds.maxZ);
    }
}
