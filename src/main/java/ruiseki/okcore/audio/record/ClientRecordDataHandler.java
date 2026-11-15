
package ruiseki.okcore.audio.record;

import net.minecraft.item.ItemRecord;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.event.resource.ResourceEvent;

public class ClientRecordDataHandler {

    public static final ClientRecordDataHandler INSTANCE = new ClientRecordDataHandler();

    private static final int MAX_CACHE_SIZE = 256;
    private static final Cache<String, RecordData> CACHE = CacheBuilder.newBuilder()
        .maximumSize(MAX_CACHE_SIZE)
        .build();

    private ClientRecordDataHandler() {}

    public static RecordData getData(ItemRecord record) {
        return record != null ? getData(record.recordName) : null;
    }

    public static RecordData getData(String recordName) {
        return recordName != null ? CACHE.getIfPresent(recordName) : null;
    }

    public static void putData(String recordName, RecordData data) {
        if (recordName == null || data == null) return;
        if (data.getDurationTicks() <= 0) return;

        CACHE.put(recordName, data);
    }

    public static boolean hasData(String recordName) {
        return recordName != null && CACHE.getIfPresent(recordName) != null;
    }

    public static void removeData(String recordName) {
        if (recordName != null) {
            CACHE.invalidate(recordName);
        }
    }

    public static void clear() {
        CACHE.invalidateAll();
    }

    @SubscribeEvent
    public void onReloadResource(ResourceEvent.Post event) {
        clear();
    }
}
