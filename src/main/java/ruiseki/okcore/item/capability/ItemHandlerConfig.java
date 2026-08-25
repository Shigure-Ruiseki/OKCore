package ruiseki.okcore.item.capability;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;
import ruiseki.okcore.item.handler.IItemHandler;

public class ItemHandlerConfig extends CapabilityConfig<IItemHandler> {

    /**
     * The unique instance.
     */
    public static ItemHandlerConfig _instance;

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public ItemHandlerConfig() {
        super(
            OKCore._instance,
            true,
            "item_handler",
            "A container or block entity that can handle and store items.",
            IItemHandler.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
