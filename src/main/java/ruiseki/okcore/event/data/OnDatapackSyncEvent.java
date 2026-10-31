
package ruiseki.okcore.event.data;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Fired when datapack-related data should be synchronized from the
 * server to connected clients.
 *
 * <p>
 * This event is dispatched when a player joins the server or when
 * server data is reloaded, before tags and crafting recipes are
 * synchronized to clients.
 * </p>
 *
 * <p>
 * Subscribers should use this event to send custom datapack data
 * to the affected players.
 * </p>
 *
 * <p>
 * Synchronization can target either an individual player or all
 * connected players:
 * </p>
 *
 * <ul>
 * <li>If {@link #getPlayer()} is non-null, synchronization targets
 * that player.</li>
 * <li>If {@link #getPlayer()} is null, synchronization targets
 * all players managed by {@link #getManager()}.</li>
 * </ul>
 *
 * <p>
 * This event is intended for server-side use. It is not cancelable
 * and does not have a result.
 * </p>
 *
 * @see ServerConfigurationManager
 * @see EntityPlayerMP
 */
public class OnDatapackSyncEvent extends Event {

    private final ServerConfigurationManager manager;

    @Nullable
    private final EntityPlayerMP player;

    /**
     * Constructs an event for synchronizing datapack data to clients.
     *
     * @param manager the server configuration manager containing the
     *                connected players
     * @param player  the target player, or {@code null} to synchronize
     *                data to all connected players
     */
    public OnDatapackSyncEvent(ServerConfigurationManager manager, @Nullable EntityPlayerMP player) {
        this.manager = manager;
        this.player = player;
    }

    /**
     * Returns the server configuration manager associated with this event.
     *
     * <p>
     * The manager provides access to the connected players and
     * other server-side player management functionality.
     * </p>
     *
     * @return the server configuration manager
     */
    public ServerConfigurationManager getManager() {
        return manager;
    }

    /**
     * Returns the individual player targeted by this synchronization.
     *
     * <p>
     * A {@code null} value indicates that the event targets all
     * connected players, such as during a server data reload.
     * </p>
     *
     * @return the target player, or {@code null} for a global sync
     */
    @Nullable
    public EntityPlayerMP getPlayer() {
        return player;
    }

    /**
     * Returns the players that should receive datapack data.
     *
     * <p>
     * If an individual player is specified, the returned list
     * contains only that player. Otherwise, the server configuration
     * manager's current player list is returned.
     * </p>
     *
     * <p>
     * When synchronizing all players, the returned list is the
     * manager's existing list rather than a defensive copy.
     * Callers should not modify it.
     * </p>
     *
     * @return the target player as a singleton list, or the list
     *         of all connected players
     */
    public List<EntityPlayerMP> getPlayers() {
        return this.player == null ? this.manager.playerEntityList : List.of(this.player);
    }
}
