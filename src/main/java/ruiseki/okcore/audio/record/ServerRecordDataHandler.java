
package ruiseki.okcore.audio.record;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemRecord;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.network.packet.PacketResetRecordData;

public class ServerRecordDataHandler {

    public static final ServerRecordDataHandler INSTANCE = new ServerRecordDataHandler();

    private static final int MAX_RECORDS_PER_PLAYER = 256;
    private static final Map<UUID, Cache<String, RecordData>> PLAYER_CACHE = new ConcurrentHashMap<>();

    private ServerRecordDataHandler() {}

    private static Cache<String, RecordData> createPlayerCache() {
        return CacheBuilder.newBuilder()
            .maximumSize(MAX_RECORDS_PER_PLAYER)
            .build();
    }

    private static Cache<String, RecordData> getPlayerCache(UUID playerId) {
        if (playerId == null) return null;
        return PLAYER_CACHE.computeIfAbsent(playerId, id -> createPlayerCache());
    }

    public static RecordData getData(UUID playerId, ItemRecord record) {
        if (record == null) return null;
        return getData(playerId, record.recordName);
    }

    public static RecordData getData(UUID playerId, String recordName) {
        if (playerId == null || recordName == null) return null;
        Cache<String, RecordData> cache = PLAYER_CACHE.get(playerId);
        return cache != null ? cache.getIfPresent(recordName) : null;
    }

    public static void putData(UUID playerId, String recordName, RecordData data) {
        if (playerId == null || recordName == null || data == null) return;
        if (data.getDurationTicks() <= 0) return;
        Cache<String, RecordData> cache = getPlayerCache(playerId);
        cache.put(recordName, data);
    }

    public static boolean hasData(UUID playerId, ItemRecord record) {
        if (record == null) return false;
        return hasData(playerId, record.recordName);
    }

    public static boolean hasData(UUID playerId, String recordName) {
        if (playerId == null || recordName == null) return false;
        Cache<String, RecordData> cache = PLAYER_CACHE.get(playerId);
        return cache != null && cache.getIfPresent(recordName) != null;
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
