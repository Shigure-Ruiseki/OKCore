package ruiseki.commoncapabilities.api.capability.wrench;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * An indicator for what is being targeted by a wrench.
 *
 * @author rubensworks
 */
public class WrenchTarget {

    private final MovingObjectPosition.MovingObjectType type;
    private final World world;
    private final BlockPos pos;
    private final ForgeDirection side;
    private final Entity entity;

    protected WrenchTarget(MovingObjectPosition.MovingObjectType type, World world, BlockPos pos, ForgeDirection side,
        Entity entity) {
        this.type = type;
        this.world = world;
        this.pos = pos;
        this.side = side;
        this.entity = entity;
    }

    public static WrenchTarget forBlock(World world, BlockPos pos, ForgeDirection side) {
        return new WrenchTarget(MovingObjectPosition.MovingObjectType.BLOCK, world, pos, side, null);
    }

    public static WrenchTarget forEntity(Entity entity) {
        return new WrenchTarget(MovingObjectPosition.MovingObjectType.ENTITY, null, null, null, entity);
    }

    public static WrenchTarget forNone() {
        return new WrenchTarget(MovingObjectPosition.MovingObjectType.MISS, null, null, null, null);
    }

    public MovingObjectPosition.MovingObjectType getType() {
        return type;
    }

    public @Nullable World getWorld() {
        return world;
    }

    public @Nullable BlockPos getPos() {
        return pos;
    }

    public @Nullable ForgeDirection getSide() {
        return side;
    }

    public @Nullable Entity getEntity() {
        return entity;
    }
}
