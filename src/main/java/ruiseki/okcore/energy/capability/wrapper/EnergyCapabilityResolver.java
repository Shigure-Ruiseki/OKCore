package ruiseki.okcore.energy.capability.wrapper;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.resolver.ICapabilityResolver;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.energy.capability.wrapper.cofh.CoFHEnergyWrapper;
import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.helper.EnderIOHelpers;

@NotNullByDefault
public class EnergyCapabilityResolver implements ICapabilityResolver {

    private final TileEntity tile;
    private final Map<ForgeDirection, LazyOptional<IEnergyStorage>> cachedCapabilities = new EnumMap<>(
        ForgeDirection.class);

    public EnergyCapabilityResolver(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public List<Capability<?>> getSupportedCapabilities() {
        return Collections.singletonList(CapabilityEnergy.ENERGY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> resolve(Capability<T> capability, @Nullable ForgeDirection side) {
        if (capability != CapabilityEnergy.ENERGY) return LazyOptional.empty();

        ForgeDirection actualSide = (side == null) ? ForgeDirection.UNKNOWN : side;

        return (LazyOptional<T>) getCachedOrResolve(actualSide, cachedCapabilities, () -> {
            if (Mods.EnderIO.isModLoaded()) {
                LazyOptional<IEnergyStorage> eio = EnderIOHelpers.getPowerCapability(tile, actualSide);
                if (eio.isPresent()) return eio;
            }
            return LazyOptional.of(() -> new CoFHEnergyWrapper(tile, actualSide));
        });
    }

    public static <T> LazyOptional<T> getCachedOrResolve(ForgeDirection side,
        Map<ForgeDirection, LazyOptional<T>> cache, Supplier<LazyOptional<T>> resolver) {
        LazyOptional<T> cached = cache.get(side);
        if (cached != null && cached.isPresent()) {
            return cached;
        }
        LazyOptional<T> resolved = resolver.get();
        cache.put(side, resolved);
        return resolved;
    }

    @Override
    public void invalidate(Capability<?> capability, @Nullable ForgeDirection side) {
        ForgeDirection actualSide = (side == null) ? ForgeDirection.UNKNOWN : side;
        LazyOptional<IEnergyStorage> cap = cachedCapabilities.remove(actualSide);
        if (cap != null && cap.isPresent()) {
            cap.invalidate();
        }
    }

    @Override
    public void invalidateAll() {
        cachedCapabilities.values()
            .forEach(cap -> { if (cap.isPresent()) cap.invalidate(); });
        cachedCapabilities.clear();
    }
}
