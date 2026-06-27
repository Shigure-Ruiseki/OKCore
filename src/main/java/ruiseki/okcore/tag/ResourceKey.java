package ruiseki.okcore.tag;

import static ruiseki.okcore.tag.Registries.ROOT_REGISTRY_NAME;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.MapMaker;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.INetworkMaterial;

public class ResourceKey<T> implements Comparable<ResourceKey<?>>, INetworkMaterial {

    private static final ConcurrentMap<ResourceKey.InternKey, ResourceKey<?>> VALUES = new MapMaker().weakValues()
        .makeMap();

    private final ResourceLocation registryName;
    private final ResourceLocation location;

    private ResourceKey(ResourceLocation registryName, ResourceLocation location) {
        this.registryName = registryName;
        this.location = location;
    }

    public static <T> ResourceKey<T> create(ResourceKey<?> parentRegistry, ResourceLocation location) {
        return create(parentRegistry.location(), location);
    }

    public static <T> ResourceKey<ResourceKey<T>> createRegistryKey(ResourceLocation registryName) {
        return create(ROOT_REGISTRY_NAME, registryName);
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<T> create(ResourceLocation registryName, ResourceLocation location) {
        return (ResourceKey<T>) VALUES.computeIfAbsent(
            new ResourceKey.InternKey(registryName, location),
            (key) -> { return new ResourceKey<>(key.registry(), key.location()); });
    }

    public boolean isFor(ResourceKey<?> registryKey) {
        return this.registryName.equals(registryKey.location());
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<ResourceKey<E>> cast(ResourceKey<?> registryKey) {
        return this.isFor(registryKey) ? Optional.of((ResourceKey<E>) this) : Optional.empty();
    }

    public ResourceLocation location() {
        return this.location;
    }

    public ResourceLocation registry() {
        return this.registryName;
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer) throws IOException {
        buffer.writeResourceLocation(this.registryName);
        buffer.writeResourceLocation(this.location);
    }

    @Override
    public void fromNetwork(ExtendedBuffer buffer) throws IOException {}

    public static <T> ResourceKey<T> read(ExtendedBuffer buffer) throws IOException {
        ResourceLocation regName = buffer.readResourceLocation();
        ResourceLocation loc = buffer.readResourceLocation();
        return create(regName, loc);
    }

    @Override
    public int hashCode() {
        int result = registryName.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceKey<?> that = (ResourceKey<?>) o;
        return this.location.equals(that.location) && this.registryName.equals(that.registryName);
    }

    @Override
    public int compareTo(@NotNull ResourceKey<?> o) {
        int ret = this.registry()
            .toString()
            .compareTo(
                o.registry()
                    .toString());
        if (ret == 0) {
            ret = this.location()
                .toString()
                .compareTo(
                    o.location()
                        .toString());
        }
        return ret;
    }

    @Override
    public String toString() {
        return "ResourceKey[" + this.registryName + " / " + this.location + "]";
    }

    static record InternKey(ResourceLocation registry, ResourceLocation location) {}
}
