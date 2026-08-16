package ruiseki.okcore.modcompat.capabilities;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.capabilities.resolver.ICapabilityResolver;
import ruiseki.okcore.datastructure.LazyOptional;

public class ConstructorCapabilityResolver<K, V> implements ICapabilityResolver {

    private final Capability<?> capability;
    private final K keyObject;
    private final V valueObject;
    private final ICapabilityConstructor<?, K, V> constructor;

    private LazyOptional<?> cachedOptional = LazyOptional.empty();

    public ConstructorCapabilityResolver(Capability<?> capability, K keyObject, V valueObject,
        ICapabilityConstructor<?, K, V> constructor) {
        this.capability = capability;
        this.keyObject = keyObject;
        this.valueObject = valueObject;
        this.constructor = constructor;
    }

    @Override
    public @NotNull List<Capability<?>> getSupportedCapabilities() {
        return Collections.singletonList(capability);
    }

    @Override
    public <T> @NotNull LazyOptional<T> resolve(@NotNull Capability<T> capability, @Nullable ForgeDirection side) {
        if (this.capability == capability) {
            if (!cachedOptional.isPresent()) {
                ICapabilityProvider provider = constructor.createProvider(keyObject, valueObject);
                if (provider != null) {
                    cachedOptional = provider.getCapability(capability, side);
                } else {
                    cachedOptional = LazyOptional.empty();
                }
            }
            return cachedOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable ForgeDirection side) {
        if (this.capability == capability && cachedOptional != null) {
            cachedOptional.invalidate();
            cachedOptional = LazyOptional.empty();
        }
    }

    @Override
    public void invalidateAll() {
        if (cachedOptional != null) {
            cachedOptional.invalidate();
            cachedOptional = LazyOptional.empty();
        }
    }
}
