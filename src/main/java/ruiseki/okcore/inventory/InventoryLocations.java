package ruiseki.okcore.inventory;

import ruiseki.okcore.OKCore;

/**
 * @author rubensworks
 */
public class InventoryLocations {

    public static IRegistryInventoryLocation REGISTRY = OKCore._instance.getRegistryManager()
        .getRegistry(IRegistryInventoryLocation.class);

    static {
        REGISTRY.register(InventoryLocationPlayer.getInstance());
    }

}
