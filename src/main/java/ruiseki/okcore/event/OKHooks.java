
package ruiseki.okcore.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

import ruiseki.okcore.event.interaction.InteractionResult;
import ruiseki.okcore.event.interaction.PlayerInteractEvent;

/**
 * Utility hooks for dispatching custom gameplay interaction events.
 *
 * <p>
 * This class provides entry points for firing OKCore interaction events
 * through the Forge event bus and retrieving their cancellation results.
 * </p>
 *
 * <p>
 * The hooks do not directly perform the underlying interaction.
 * They only dispatch the corresponding event and return its
 * cancellation result when applicable.
 * </p>
 *
 * @see PlayerInteractEvent
 * @see InteractionResult
 */
public final class OKHooks {

    private OKHooks() {}

    /**
     * Fires an entity-specific interaction event using a ray trace result.
     *
     * <p>
     * The world-space hit position provided by {@code ray.hitVec}
     * is converted into coordinates relative to the target entity's
     * position before dispatching the event.
     * </p>
     *
     * <p>
     * The converted position is passed to
     * {@link #onInteractEntityAt(EntityPlayer, Entity, Vec3)}.
     * </p>
     *
     * @param player the player performing the interaction
     * @param entity the entity being interacted with
     * @param ray    the ray trace result containing the hit position
     * @return the event's cancellation result if canceled,
     *         or {@code null} otherwise
     *
     * @see MovingObjectPosition
     * @see Vec3
     */
    public static InteractionResult onInteractEntityAt(EntityPlayer player, Entity entity, MovingObjectPosition ray) {
        Vec3 hitVec = ray.hitVec;
        Vec3 localPos = Vec3
            .createVectorHelper(hitVec.xCoord - entity.posX, hitVec.yCoord - entity.posY, hitVec.zCoord - entity.posZ);
        return onInteractEntityAt(player, entity, localPos);
    }

    /**
     * Fires an entity-specific interaction event at the given local position.
     *
     * <p>
     * Creates and posts a {@link PlayerInteractEvent.EntityInteractSpecific}
     * event to the Forge event bus.
     * Registered listeners may cancel the interaction and provide a custom
     * {@link InteractionResult}.
     * </p>
     *
     * <p>
     * If the event is canceled, its cancellation result is returned.
     * Otherwise, {@code null} is returned to indicate that no
     * cancellation result was provided.
     * </p>
     *
     * @param player   the player performing the interaction
     * @param entity   the entity being interacted with
     * @param localPos the hit position relative to the entity's position
     * @return the cancellation result if the event was canceled,
     *         or {@code null} otherwise
     *
     * @see PlayerInteractEvent.EntityInteractSpecific
     * @see InteractionResult
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
