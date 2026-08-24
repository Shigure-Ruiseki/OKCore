package ruiseki.okcore.fluid.capability;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;
import ruiseki.okcore.fluid.handler.IFluidHandlerItem;

public class FluidHandlerItemConfig extends CapabilityConfig<IFluidHandlerItem> {

    /**
     * The unique instance.
     */
    public static FluidHandlerItemConfig _instance;

    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public FluidHandlerItemConfig() {
        super(
            OKCore._instance,
            true,
            "fluid_handler_item",
            "An item stack container that can handle and store fluids.",
            IFluidHandlerItem.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
