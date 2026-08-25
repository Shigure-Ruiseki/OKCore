package ruiseki.okcore.fluid.capability;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;
import ruiseki.okcore.fluid.handler.IFluidHandler;

public class FluidHandlerConfig extends CapabilityConfig<IFluidHandler> {

    /**
     * The unique instance.
     */
    public static FluidHandlerConfig _instance;

    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public FluidHandlerConfig() {
        super(
            OKCore._instance,
            true,
            "fluid_handler",
            "A container or block entity that can handle and store fluids.",
            IFluidHandler.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
