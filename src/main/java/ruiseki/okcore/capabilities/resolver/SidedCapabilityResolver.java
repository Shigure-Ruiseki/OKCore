package ruiseki.okcore.capabilities.resolver;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.EnumFacingMap;
import ruiseki.okcore.datastructure.LazyOptional;

@NotNullByDefault
public class SidedCapabilityResolver<T> implements ICapabilityResolver {

    public static <T> SidedCapabilityResolver<T> create(Capability<T> supportedCapability,
        Function<ForgeDirection, T> supplier) {
        return new SidedCapabilityResolver<>(supportedCapability, supplier);
    }

    private final List<Capability<?>> supportedCapability;
    private final Function<ForgeDirection, T> provider;
    private final EnumFacingMap<LazyOptional<T>> cachedCapabilities = EnumFacingMap.newMap();

    protected SidedCapabilityResolver(Capability<T> supportedCapability, Function<ForgeDirection, T> provider) {
        this.supportedCapability = Collections.singletonList(supportedCapability);
        this.provider = provider;
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return supportedCapability;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> LazyOptional<R> resolve(Capability<R> capability, @Nullable ForgeDirection side) {
        if (side == null) {
            return LazyOptional.empty();
        }

        LazyOptional<T> cached = cachedCapabilities.get(side);
        if (cached == null || !cached.isPresent()) {
            T value = provider.apply(side);
            if (value == null) {
                return LazyOptional.empty();
            }
            cached = LazyOptional.of(() -> value);
            cachedCapabilities.put(side, cached);
        }

        return (LazyOptional<R>) cached;
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable ForgeDirection side) {
        if (side != null) {
            LazyOptional<T> cached = cachedCapabilities.remove(side);
            if (cached != null && cached.isPresent()) {
                cached.invalidate();
            }
        } else {
            invalidateAll();
        }
    }

    @Override
    public void invalidateAll() {
        for (LazyOptional<T> cached : cachedCapabilities.values()) {
            if (cached != null && cached.isPresent()) {
                cached.invalidate();
            }
        }
        cachedCapabilities.clear();
    }
}
