package ruiseki.okcore.item.capability.wrapper;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.resolver.ICapabilityResolver;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;

@NotNullByDefault
public class ItemHandlerResolver implements ICapabilityResolver {

    private final IInventory inventory;
    private final Map<ForgeDirection, LazyOptional<IItemHandler>> cache = new EnumMap<>(ForgeDirection.class);

    public ItemHandlerResolver(IInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return Collections.singletonList(CapabilityItemHandler.ITEM_HANDLER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable ForgeDirection side) {
        if (capability != CapabilityItemHandler.ITEM_HANDLER) return LazyOptional.empty();

        ForgeDirection actualSide = (side == null) ? ForgeDirection.UNKNOWN : side;

        return (LazyOptional<T>) getCachedOrResolve(
            actualSide,
            cache,
            () -> LazyOptional.of(() -> new InventoryHandlerWrapper(inventory, actualSide)));
    }

    private static <T> LazyOptional<T> getCachedOrResolve(ForgeDirection side,
        Map<ForgeDirection, LazyOptional<T>> cache, java.util.function.Supplier<LazyOptional<T>> resolver) {
        LazyOptional<T> cached = cache.get(side);
        if (cached != null && cached.isPresent()) return cached;
        LazyOptional<T> resolved = resolver.get();
        cache.put(side, resolved);
        return resolved;
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable ForgeDirection side) {
        ForgeDirection actualSide = (side == null) ? ForgeDirection.UNKNOWN : side;
        LazyOptional<IItemHandler> cap = cache.remove(actualSide);
        if (cap != null && cap.isPresent()) cap.invalidate();
    }

    @Override
    public void invalidateAll() {
        cache.values()
            .forEach(cap -> { if (cap.isPresent()) cap.invalidate(); });
        cache.clear();
    }
}
