package ruiseki.okcore.tag.entry;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.discovery.ASMDataTable;
import ruiseki.okcore.OKCore;

public class TagEntryRegistry {

    private static final Map<String, TagEntry<?>> FACTORIES = new HashMap<>();

    public static void registerFactory(String id, TagEntry<?> factoryInstance) {
        FACTORIES.put(id, factoryInstance);
    }

    @SuppressWarnings("unchecked")
    public static <T> TagEntry<T> getFactory(String subfolder) {
        if (subfolder == null) return null;
        return (TagEntry<T>) FACTORIES.get(subfolder);
    }

    public static void loadFromASM(ASMDataTable dataTable) {
        for (ASMDataTable.ASMData data : dataTable.getAll(TagData.class.getCanonicalName())) {
            try {
                Class<?> clazz = Class.forName(data.getClassName());
                if (!TagEntry.class.isAssignableFrom(clazz)) continue;

                TagData annotation = clazz.getAnnotation(TagData.class);
                if (annotation == null) continue;
                Object instance = clazz.getDeclaredConstructor()
                    .newInstance();

                if (instance instanceof TagEntry<?>factory) {
                    registerFactory(factory.getKey(), factory);
                    OKCore.okLog(
                        Level.INFO,
                        "Successfully registered TagData component for subfolder [{}]: [{}]",
                        factory.getKey(),
                        data.getClassName());
                }
            } catch (Exception e) {
                OKCore.okLog(
                    Level.ERROR,
                    "Failed to initialize ASM TagEntryHandler [{}]: {}",
                    data.getClassName(),
                    e.toString());
            }
        }
    }
}
