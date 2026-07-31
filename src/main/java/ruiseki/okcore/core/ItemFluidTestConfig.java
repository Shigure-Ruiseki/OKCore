package ruiseki.okcore.core;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

public class ItemFluidTestConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemFluidTestConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemFluidTestConfig() {
        super(OKCore._instance, true, "fluid_test", null, ItemFluidTest.class);
    }

}
