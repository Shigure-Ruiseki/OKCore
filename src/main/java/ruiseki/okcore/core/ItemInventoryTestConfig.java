package ruiseki.okcore.core;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

public class ItemInventoryTestConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemInventoryTestConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemInventoryTestConfig() {
        super(OKCore._instance, true, "inventory_test", null, ItemInventoryTest.class);
    }

}
