
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

    private static final RecordData DEFAULT_DATA = new RecordData(null, 300 * 20, 44100, 300L * 44100L);

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
        if (recordName == null) return DEFAULT_DATA;
        RecordData data = CACHE.getIfPresent(recordName);
        return data != null ? data : DEFAULT_DATA;
    }

    public static int getDurationTicks(ItemRecord record) {
        RecordData data = getData(record);
        return data != null ? data.getDurationTicks() + 100 : 0;
    }

    public static void putData(String recordName, RecordData data) {
        if (recordName == null || data == null || data.getDurationTicks() <= 0) return;
        CACHE.put(recordName, data);
    }
}
