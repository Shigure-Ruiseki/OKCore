
package ruiseki.okcore.audio.record;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemRecord;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.network.packet.PacketResetRecordData;

public class ServerRecordDataHandler {

    public static ServerRecordDataHandler INSTANCE = new ServerRecordDataHandler();

    private static final Map<UUID, Map<String, RecordData>> PLAYER_CACHE = new ConcurrentHashMap<>();

    private ServerRecordDataHandler() {}

    private static Map<String, RecordData> getPlayerCache(UUID playerId) {
        if (playerId == null) return null;

        return PLAYER_CACHE.computeIfAbsent(playerId, id -> new ConcurrentHashMap<>());
    }

    public static RecordData getData(UUID playerId, ItemRecord record) {
        if (record == null) return null;
        return getData(playerId, record.recordName);
    }

    public static RecordData getData(UUID playerId, String recordName) {
        if (recordName == null) return null;
        Map<String, RecordData> cache = PLAYER_CACHE.get(playerId);
        return cache != null ? cache.get(recordName) : null;
    }

    public static void putData(UUID playerId, String recordName, RecordData data) {
        if (playerId == null || recordName == null || data == null) return;
        if (data.getDurationTicks() <= 0) return;
        getPlayerCache(playerId).put(recordName, data);
    }

    public static boolean hasData(UUID playerId, ItemRecord record) {
        if (record == null) return false;
        return hasData(playerId, record.recordName);
    }

    public static boolean hasData(UUID playerId, String recordName) {
        if (recordName == null) return false;
        Map<String, RecordData> cache = PLAYER_CACHE.get(playerId);
        return cache != null && cache.containsKey(recordName);
    }

    public static void removeData(UUID playerId, String recordName) {
        if (playerId == null || recordName == null) return;
        Map<String, RecordData> cache = PLAYER_CACHE.get(playerId);
        if (cache != null) {
            cache.remove(recordName);
            if (cache.isEmpty()) {
                PLAYER_CACHE.remove(playerId, cache);
            }
        }
    }

    public static void removePlayer(UUID playerId) {
        if (playerId != null) {
            PLAYER_CACHE.remove(playerId);
        }
    }

    public static void clear() {
        PLAYER_CACHE.clear();
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerRecordDataHandler.removePlayer(event.player.getUniqueID());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP player)) return;
        OKCore._instance.getPacketHandler()
            .sendToPlayer(new PacketResetRecordData(), player);
        ServerRecordDataHandler.removePlayer(event.player.getUniqueID());
    }
}
