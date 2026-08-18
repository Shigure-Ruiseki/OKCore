package ruiseki.commoncapabilities.modcompat.mekansim.item;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

public class ItemGasTestConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemGasTestConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemGasTestConfig() {
        super(CommonCapabilities._instance, true, "gas_test", null, itemConfig -> new ItemGasTest());
    }

}
