package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityCache;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;

public class FluidHandlerWrapperProvider implements ICapabilityProvider {

    private final CapabilityCache cache = new CapabilityCache();

    public FluidHandlerWrapperProvider(net.minecraftforge.fluids.IFluidHandler handler) {
        cache.addCapabilityResolver(new FluidCapabilityResolver(handler));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        return cache.getCapability(cap, side);
    }
}
