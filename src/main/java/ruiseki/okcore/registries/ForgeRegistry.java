package ruiseki.okcore.registries;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

public class ForgeRegistry<V extends IForgeRegistryEntry<V>> implements IForgeRegistry<V> {

    private final Class<V> superType;
    private final ResourceLocation name;
    private final ResourceLocation defaultKey;

    private final Map<ResourceLocation, V> entries = new LinkedHashMap<>();

    public ForgeRegistry(Class<V> superType, ResourceLocation name, @Nullable ResourceLocation defaultKey) {
        this.superType = superType;
        this.name = name;
        this.defaultKey = defaultKey;
    }

    @Override
    public Class<V> getRegistrySuperType() {
        return superType;
    }

    @Override
    public void register(V value) {
        if (value == null) {
            throw new NullPointerException("Cannot register a null object into registry: " + name);
        }
        ResourceLocation key = value.getRegistryName();
        if (key == null) {
            throw new IllegalArgumentException(
                "Registry name is not set for " + value.getClass()
                    .getName());
        }
        if (entries.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate registration for key: " + key + " in registry " + name);
        }
        entries.put(key, value);
    }

    @SafeVarargs
    @Override
    public final void registerAll(V... values) {
        for (V val : values) {
            register(val);
        }
    }

    @Override
    public boolean containsKey(ResourceLocation key) {
        return entries.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return entries.containsValue(value);
    }

    @Nullable
    @Override
    public V getValue(ResourceLocation key) {
        V value = entries.get(key);
        if (value == null && defaultKey != null) {
            return entries.get(defaultKey);
        }
        return value;
    }

    @Nullable
    @Override
    public ResourceLocation getKey(V value) {
        return value.getRegistryName();
    }

    @Nonnull
    @Override
    public Set<ResourceLocation> getKeys() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    @Nonnull
    @Override
    public Collection<V> getValuesCollection() {
        return Collections.unmodifiableCollection(entries.values());
    }

    @Nonnull
    @Override
    public Set<Map.Entry<ResourceLocation, V>> getEntries() {
        return Collections.unmodifiableSet(entries.entrySet());
    }

    @Override
    public Iterator<V> iterator() {
        return entries.values()
            .iterator();
    }

    public ResourceLocation getName() {
        return name;
    }

    ForgeRegistry<V> copy() {
        return new ForgeRegistry<V>(superType, name, defaultKey);
    }

    RegistryEvent.Register<V> getRegisterEvent(ResourceLocation name) {
        return new RegistryEvent.Register<V>(name, this);
    }

}
