package ruiseki.okcore.fluid.capability.wrapper;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.IFluidHandler;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;

public class FluidHandlerWrapperProvider implements ICapabilityProvider {

    protected final net.minecraftforge.fluids.IFluidHandler handler;
    @SuppressWarnings("unchecked")
    private final LazyOptional<IFluidHandler>[] wrappers = new LazyOptional[7];

    public FluidHandlerWrapperProvider(net.minecraftforge.fluids.IFluidHandler handler) {
        this.handler = handler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable ForgeDirection side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            ForgeDirection dir = side != null ? side : ForgeDirection.UNKNOWN;
            int index = dir.ordinal();

            if (wrappers[index] == null) {
                wrappers[index] = LazyOptional.of(() -> new FluidHandlerWrapper(handler, dir));
            }
            return wrappers[index].cast();
        }
        return LazyOptional.empty();
    }
}
