package ruiseki.okcore.item.capability;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.item.handler.IItemHandler;

public class CapabilityItemHandler {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER = null;
}
