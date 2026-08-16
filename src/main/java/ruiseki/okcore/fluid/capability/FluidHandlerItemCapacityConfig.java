package ruiseki.okcore.fluid.capability;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;
import ruiseki.okcore.fluid.handler.IFluidHandlerItemCapacity;

/**
 * Config for the item fluid handler with configurable capacity capability.
 * 
 * @author rubensworks
 *
 */
public class FluidHandlerItemCapacityConfig extends CapabilityConfig<IFluidHandlerItemCapacity> {

    /**
     * The unique instance.
     */
    public static FluidHandlerItemCapacityConfig _instance;

    @CapabilityInject(IFluidHandlerItemCapacity.class)
    public static Capability<IFluidHandlerItemCapacity> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public FluidHandlerItemCapacityConfig() {
        super(
            OKCore._instance,
            true,
            "fluid_handler_capacity",
            "Item fluid handler with configurable capacity",
            IFluidHandlerItemCapacity.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
