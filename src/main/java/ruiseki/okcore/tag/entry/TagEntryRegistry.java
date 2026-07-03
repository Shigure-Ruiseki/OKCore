package ruiseki.okcore.tag.entry;

import java.util.HashMap;
import java.util.Map;

public class TagEntryRegistry {

    private static final Map<String, ITagEntrySerializer<?, ?>> SERIALIZERS = new HashMap<>();

    static {
        register(BlockTagEntry.Serializer.INSTANCE);
        register(ItemTagEntry.Serializer.INSTANCE);
        register(FluidTagEntry.Serializer.INSTANCE);
        register(EntityTagEntry.Serializer.INSTANCE);
    }

    public static void register(ITagEntrySerializer<?, ?> serializer) {
        SERIALIZERS.put(serializer.getKey(), serializer);
    }

    public static ITagEntrySerializer<?, ?> getSerializer(String key) {
        return SERIALIZERS.get(key);
    }
}
