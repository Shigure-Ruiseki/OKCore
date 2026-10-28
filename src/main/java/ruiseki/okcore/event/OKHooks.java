
package ruiseki.okcore.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

import ruiseki.okcore.event.interaction.InteractionResult;
import ruiseki.okcore.event.interaction.PlayerInteractEvent;

public final class OKHooks {

    private OKHooks() {}

    /**
     * Fires an entity-specific interaction event using
     * the hit position from a ray trace.
     *
     * @param player The interacting player.
     * @param entity The target entity.
     * @param ray    The ray trace result.
     * @return The cancellation result, or null if not canceled.
     */
    public static InteractionResult onInteractEntityAt(EntityPlayer player, Entity entity, MovingObjectPosition ray) {
        Vec3 hitVec = ray.hitVec;
        Vec3 localPos = Vec3
            .createVectorHelper(hitVec.xCoord - entity.posX, hitVec.yCoord - entity.posY, hitVec.zCoord - entity.posZ);
        return onInteractEntityAt(player, entity, localPos);
    }

    /**
     * Fires an entity-specific interaction event.
     *
     * @param player   The interacting player.
     * @param entity   The target entity.
     * @param localPos The hit position relative to the entity.
     * @return The cancellation result, or null if not canceled.
     */
    public static InteractionResult onInteractEntityAt(EntityPlayer player, Entity entity, Vec3 localPos) {
        PlayerInteractEvent.EntityInteractSpecific event = new PlayerInteractEvent.EntityInteractSpecific(
            player,
            entity,
            localPos);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isCanceled() ? event.getCancellationResult() : null;
    }
}
