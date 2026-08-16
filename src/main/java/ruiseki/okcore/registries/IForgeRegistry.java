package ruiseki.okcore.registries;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;

public interface IForgeRegistry<V extends IForgeRegistryEntry<V>> extends Iterable<V> {

    Class<V> getRegistrySuperType();

    void register(V value);

    void registerAll(V... values);

    boolean containsKey(ResourceLocation key);

    boolean containsValue(V value);

    @Nullable
    V getValue(ResourceLocation key);

    @Nullable
    ResourceLocation getKey(V value);

    @Nonnull
    Set<ResourceLocation> getKeys();

    @Nonnull
    Collection<V> getValuesCollection();

    @Nonnull
    Set<Map.Entry<ResourceLocation, V>> getEntries();
}
