package ruiseki.commoncapabilities.capability.itemhandler;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.api.capability.itemhandler.ISlotlessItemHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the slotless item handler capability.
 * 
 * @author rubensworks
 */
public class SlotlessItemHandlerConfig extends CapabilityConfig<ISlotlessItemHandler> {

    /**
     * The unique instance.
     */
    public static SlotlessItemHandlerConfig _instance;

    @CapabilityInject(ISlotlessItemHandler.class)
    public static Capability<ISlotlessItemHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public SlotlessItemHandlerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "slotlessItemHandler",
            "An item handler that is slot agnostic",
            ISlotlessItemHandler.class);
    }
}
