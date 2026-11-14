
package ruiseki.okcore.client.audio.record;

import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ruiseki.okcore.helper.ItemHelpers;

@SuppressWarnings("UnstableApiUsage")
public class RecordDataRegistry {

    private static final Cache<String, RecordData> CACHE = CacheBuilder.newBuilder()
        .maximumSize(256)
        .build();

    public static RecordData getData(ItemStack record) {
        if (ItemHelpers.isEmpty(record) || !(ItemHelpers.getItem(record) instanceof ItemRecord itemRecord)) return null;
        return getData(itemRecord);
    }

    public static int getDurationTicks(ItemStack record) {
        if (ItemHelpers.isEmpty(record) || !(ItemHelpers.getItem(record) instanceof ItemRecord itemRecord)) return 0;
        return getDurationTicks(itemRecord);
    }

    public static RecordData getData(ItemRecord record) {
        String recordName = record.recordName;
        if (recordName == null) return null;
        RecordData data = CACHE.getIfPresent(recordName);
        return data != null ? data : null;
    }

    public static int getDurationTicks(ItemRecord record) {
        RecordData data = getData(record);
        return data != null ? data.getDurationTicks() + 100 : 0;
    }

    public static void putData(String recordName, RecordData data) {
        if (recordName == null || data == null || data.getDurationTicks() <= 0) return;
        CACHE.put(recordName, data);
    }

    public static boolean hasData(ItemStack record) {
        if (ItemHelpers.isEmpty(record) || !(ItemHelpers.getItem(record) instanceof ItemRecord itemRecord))
            return false;
        return hasData(itemRecord);
    }

    public static boolean hasData(ItemRecord record) {
        return record != null && record.recordName != null && CACHE.getIfPresent(record.recordName) != null;
    }

    public static boolean hasData(String record) {
        return CACHE.getIfPresent(record) != null;
    }
}
