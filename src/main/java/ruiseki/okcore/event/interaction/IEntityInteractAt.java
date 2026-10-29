
package ruiseki.okcore.event.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

/**
 * Defines a handler for player interactions at a specific position
 * on an entity.
 *
 * <p>
 * Implementations may override {@link #interactAt(EntityPlayer, Vec3)}
 * to provide custom behavior when a player interacts with an entity
 * at a particular location.
 * </p>
 *
 * <p>
 * The interaction position is expressed in coordinates relative to
 * the target entity's position rather than world coordinates.
 * </p>
 *
 * <p>
 * By default, the interaction returns {@link InteractionResult#PASS},
 * indicating that no custom action was performed and normal
 * interaction processing may continue.
 * </p>
 *
 * @see InteractionResult
 */
public interface IEntityInteractAt {

    /**
     * Handles a player interaction at a specific position on an entity.
     *
     * <p>
     * Implementations can inspect the interaction position and return
     * an appropriate result to indicate whether the interaction was
     * handled, rejected, or should continue through normal processing.
     * </p>
     *
     * @param player the player performing the interaction
     * @param vec3   the interaction position relative to the entity
     * @return the interaction result, or {@link InteractionResult#PASS}
     *         by default
     *
     * @see InteractionResult
     */
    default InteractionResult interactAt(EntityPlayer player, Vec3 vec3) {
        return InteractionResult.PASS;
    }
}
