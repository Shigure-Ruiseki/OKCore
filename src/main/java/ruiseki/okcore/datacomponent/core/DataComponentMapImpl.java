package ruiseki.okcore.datacomponent.core;

import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.ApiStatus;

import com.gtnewhorizon.gtnhlib.util.ObjectPooler;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.datacomponent.registry.DataComponentRegistry;

@SuppressWarnings({ "resource", "unchecked" })
@ApiStatus.Internal
public class DataComponentMapImpl implements DataComponentMap {

    private Item item;

    private final Map<String, DataComponentType<?>> components = new Object2ObjectOpenHashMap<>(4);
    private final Map<DataComponentType<?>, Object> values = new Object2ObjectOpenHashMap<>(4);

    private static final ObjectPooler<DataComponentMapImpl> POOL = new ObjectPooler<>(DataComponentMapImpl::new);

    public static DataComponentMapImpl getInstance() {
        return POOL.getInstance()
            .assertIsDefault();
    }

    public DataComponentMapImpl assertIsDefault() {
        if (item != null || !components.isEmpty() || !values.isEmpty()) {
            throw new RuntimeException("DataComponentImpl reference was mutated/dirty while in the pool!");
        }
        return this;
    }

    public DataComponentMapImpl reset() {
        this.item = null;
        this.components.clear();
        this.values.clear();
        return this;
    }

    public DataComponentMapImpl copy(DataComponentMapImpl other) {
        reset();

        this.item = other.item;
        this.components.putAll(other.components);
        this.values.putAll(other.values);

        return this;
    }

    public DataComponentMapImpl fromStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            throw new IllegalArgumentException("ItemStack/Item cannot be null");
        }

        this.item = stack.getItem();

        DataComponentRegistry.getComponents(stack, this.components);

        for (Map.Entry<String, DataComponentType<?>> entry : components.entrySet()) {
            DataComponentType<?> type = entry.getValue();
            this.values.put(type, type.getValue(stack));
        }

        return this;
    }

    @Override
    public DataComponentMap clone() {
        DataComponentMapImpl cloned = POOL.getInstance()
            .reset();
        cloned.item = this.item;
        cloned.components.putAll(this.components);
        cloned.values.putAll(this.values);
        return cloned;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public <T> T get(DataComponentType<T> type) {
        return (T) values.get(type);
    }

    @Override
    public <T> T get(String name) {
        DataComponentType<?> component = components.get(name);
        return (component == null) ? null : (T) values.get(component);
    }

    @Override
    public <T> void set(DataComponentType<T> type, T value) {
        if (components.containsValue(type)) {
            values.put(type, value);
        }
    }

    @Override
    public <T> void set(String name, T value) {
        DataComponentType<T> type = (DataComponentType<T>) components.get(name);
        if (type == null) {
            OKCore.okLog(Level.WARN, "Invalid property name: {}", name);
            return;
        }

        Class<T> clazz = (Class<T>) type.getType();
        if (value != null && !clazz.isInstance(value)) {
            OKCore.okLog(
                Level.WARN,
                "Incompatible type for {}. Expected {}, got {}",
                name,
                clazz.getSimpleName(),
                value.getClass()
                    .getSimpleName());
            return;
        }

        values.put(type, value);
    }

    @Override
    public <T> boolean has(DataComponentType<T> type) {
        return values.containsKey(type);
    }

    @Override
    public <T> boolean has(String name) {
        return components.containsKey(name);
    }

    @Override
    public Map<String, String> toMap() {
        Map<String, String> out = new Object2ObjectOpenHashMap<>(values.size());

        components.forEach((name, prop) -> {
            Object value = values.get(prop);

            if (value != null) {
                out.put(name, ((DataComponentType<Object>) prop).stringify(value));
            }
        });

        return out;
    }

    @Override
    public void close() {
        POOL.releaseInstance(this.reset());
    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + values.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof DataComponentMapImpl other)) return false;

        return this.item == other.item && this.values.equals(other.values);
    }
}
