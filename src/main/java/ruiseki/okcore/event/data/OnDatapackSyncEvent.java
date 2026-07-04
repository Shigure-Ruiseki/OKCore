package ruiseki.okcore.event.data;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ServerConfigurationManager;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Fires when a player joins the server or when the reload command is ran,
 * before tags and crafting recipes are sent to the client. Send datapack data
 * to clients when this event fires.
 */
public class OnDatapackSyncEvent extends Event {

    private final ServerConfigurationManager manager;
    @Nullable
    private final EntityPlayerMP player;

    public OnDatapackSyncEvent(ServerConfigurationManager manager, @Nullable EntityPlayerMP player) {
        this.manager = manager;
        this.player = player;
    }

    /**
     * @return The server configuration manager to get a view of all players.
     */
    public ServerConfigurationManager getManager() {
        return manager;
    }

    /**
     * @return The player to sync datapacks to. Null when syncing for all players,
     *         such as when the reload command runs.
     */
    @Nullable
    public EntityPlayerMP getPlayer() {
        return player;
    }

    /**
     * @return A list of players that should receive data during this event, which is the specified player (if not null)
     *         or all players otherwise.
     */
    public List<EntityPlayerMP> getPlayers() {
        return this.player == null ? this.manager.playerEntityList : List.of(this.player);
    }
}
