package ruiseki.okcore.tag;

import java.io.IOException;
import java.util.Optional;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.network.INetworkMaterial;

public record TagKey<T> (ResourceKey<?> registry, ResourceLocation location)
    implements Comparable<TagKey<?>>, INetworkMaterial {

    private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

    @SuppressWarnings("unchecked")
    public static <T> TagKey<T> create(ResourceKey<?> registryKey, ResourceLocation location) {
        return (TagKey<T>) VALUES.intern(new TagKey<>(registryKey, location));
    }

    public boolean isFor(ResourceKey<?> registryKey) {
        return this.registry == registryKey;
    }

    @SuppressWarnings("unchecked")
    public <E> Optional<TagKey<E>> cast(ResourceKey<?> registryKey) {
        return this.isFor(registryKey) ? Optional.of((TagKey<E>) this) : Optional.empty();
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer) throws IOException {
        this.registry.toNetwork(buffer);
        buffer.writeResourceLocation(this.location);
    }

    @Override
    public void fromNetwork(ExtendedBuffer buffer) throws IOException {}

    public static <T> TagKey<T> read(ExtendedBuffer buffer) throws IOException {
        ResourceKey<?> regKey = ResourceKey.read(buffer);
        ResourceLocation loc = buffer.readResourceLocation();
        return create(regKey, loc);
    }

    @Override
    public int compareTo(@NotNull TagKey<?> o) {
        int ret = this.registry.location()
            .toString()
            .compareTo(
                o.registry.location()
                    .toString());
        if (ret == 0) {
            ret = this.location.toString()
                .compareTo(o.location.toString());
        }
        return ret;
    }

    @Override
    public @NotNull String toString() {
        return "TagKey[" + this.registry.location() + " / " + this.location + "]";
    }
}
