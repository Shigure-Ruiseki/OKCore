package ruiseki.okcore;

import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.core.ItemEnergyTestConfig;
import ruiseki.okcore.core.ItemFluidTestConfig;
import ruiseki.okcore.core.ItemInventoryTestConfig;

public class Configs {

    public static void register(ConfigHandler configHandler) {
        configHandler.add(new ItemEnergyTestConfig());
        configHandler.add(new ItemFluidTestConfig());
        configHandler.add(new ItemInventoryTestConfig());
    }
}
