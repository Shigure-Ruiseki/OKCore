package ruiseki.okcore.core.item.gui;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

public class ItemGuiTestConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemGuiTestConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemGuiTestConfig() {
        super(OKCore._instance, true, "gui_test", null, itemConfig -> new ItemGuiTest());
    }

}
