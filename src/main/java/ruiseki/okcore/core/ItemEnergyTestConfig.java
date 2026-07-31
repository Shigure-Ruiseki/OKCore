package ruiseki.okcore.core;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

public class ItemEnergyTestConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemEnergyTestConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemEnergyTestConfig() {
        super(OKCore._instance, true, "energy_test", null, ItemEnergyTest.class);
    }

}
