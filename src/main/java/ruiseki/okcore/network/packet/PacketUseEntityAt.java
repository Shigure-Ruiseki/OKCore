package ruiseki.okcore.network.packet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import ruiseki.okcore.event.OKHooks;
import ruiseki.okcore.event.interaction.IEntityInteractAt;
import ruiseki.okcore.event.interaction.InteractionResult;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketUseEntityAt extends PacketCodec {

    @CodecField
    public int entityId;
    @CodecField
    public boolean isSneaking;
    @CodecField
    public Vec3 location;

    public PacketUseEntityAt() {

    }

    public PacketUseEntityAt(Entity entity, boolean isSneaking, Vec3 location) {
        this(entity.getEntityId(), isSneaking, location);
    }

    public PacketUseEntityAt(int entityId, boolean isSneaking, Vec3 location) {
        this.entityId = entityId;
        this.isSneaking = isSneaking;
        this.location = location;
    }

    public Entity getEntity(World worldIn) {
        return worldIn.getEntityByID(this.entityId);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (world == null || player == null || location == null) return;
        Entity entity = getEntity(world);
        if (entity == null || entity.isDead) return;
        player.func_143004_u();
        boolean visible = player.canEntityBeSeen(entity);
        double maxDistanceSq = visible ? 36.0D : 9.0D;
        if (player.getDistanceSqToEntity(entity) >= maxDistanceSq) return;
        double x = location.xCoord;
        double y = location.yCoord;
        double z = location.zCoord;
        if (Double.isNaN(x) || Double
            .isInfinite(x) || Double.isNaN(y) || Double.isInfinite(y) || Double.isNaN(z) || Double.isInfinite(z)) {
            return;
        }
        InteractionResult result = OKHooks.onInteractEntityAt(player, entity, location);
        if (result != null) return;
        if (entity instanceof IEntityInteractAt) {
            ((IEntityInteractAt) entity).interactAt(player, location);
        }
    }
}
