package ruiseki.okcore.energy.capability.wrapper;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityCache;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;

public class EnergyStorageWrapper implements ICapabilityProvider {

    private final CapabilityCache cache = new CapabilityCache();

    public EnergyStorageWrapper(TileEntity tile) {
        cache.addCapabilityResolver(new EnergyCapabilityResolver(tile));
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        return cache.getCapability(cap, side);
    }
}
