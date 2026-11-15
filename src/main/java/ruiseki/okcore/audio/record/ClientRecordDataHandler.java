
package ruiseki.okcore.audio.record;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.item.ItemRecord;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.event.resource.ResourceEvent;

public class ClientRecordDataHandler {

    public static ClientRecordDataHandler INSTANCE = new ClientRecordDataHandler();

    private static final Map<String, RecordData> CACHE = new ConcurrentHashMap<>();

    public ClientRecordDataHandler() {}

    public static RecordData getData(ItemRecord record) {
        return getData(record.recordName);
    }

    public static RecordData getData(String recordName) {
        return recordName != null ? CACHE.get(recordName) : null;
    }

    public static void putData(String recordName, RecordData data) {
        if (recordName == null || data == null || data.getDurationTicks() <= 0) return;
        CACHE.put(recordName, data);
    }

    public static boolean hasData(String recordName) {
        return recordName != null && CACHE.containsKey(recordName);
    }

    public static void removeData(String recordName) {
        if (recordName != null) CACHE.remove(recordName);
    }

    public static void clear() {
        CACHE.clear();
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onReloadResource(ResourceEvent.Post event) {
        ClientRecordDataHandler.clear();
    }
}
