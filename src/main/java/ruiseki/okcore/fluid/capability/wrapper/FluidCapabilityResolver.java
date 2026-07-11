package ruiseki.okcore.fluid.capability.wrapper;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.resolver.ICapabilityResolver;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.IFluidHandler;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;

@NotNullByDefault
public class FluidCapabilityResolver implements ICapabilityResolver {

    private final net.minecraftforge.fluids.IFluidHandler handler;
    private final Map<ForgeDirection, LazyOptional<IFluidHandler>> cache = new EnumMap<>(ForgeDirection.class);

    public FluidCapabilityResolver(net.minecraftforge.fluids.IFluidHandler handler) {
        this.handler = handler;
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return List.of(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable ForgeDirection side) {
        if (capability != CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) return LazyOptional.empty();

        ForgeDirection actualSide = (side == null) ? ForgeDirection.UNKNOWN : side;

        return (LazyOptional<T>) getCachedOrResolve(
            actualSide,
            cache,
            () -> LazyOptional.of(() -> new FluidHandlerWrapper(handler, actualSide)));
    }

    private static <T> LazyOptional<T> getCachedOrResolve(ForgeDirection side,
        Map<ForgeDirection, LazyOptional<T>> cache, Supplier<LazyOptional<T>> resolver) {
        LazyOptional<T> cached = cache.get(side);
        if (cached != null && cached.isPresent()) return cached;
        LazyOptional<T> resolved = resolver.get();
        cache.put(side, resolved);
        return resolved;
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable ForgeDirection side) {
        ForgeDirection actualSide = (side == null) ? ForgeDirection.UNKNOWN : side;
        LazyOptional<IFluidHandler> cap = cache.remove(actualSide);
        if (cap != null && cap.isPresent()) cap.invalidate();
    }

    @Override
    public void invalidateAll() {
        cache.values()
            .forEach(cap -> { if (cap.isPresent()) cap.invalidate(); });
        cache.clear();
    }
}
