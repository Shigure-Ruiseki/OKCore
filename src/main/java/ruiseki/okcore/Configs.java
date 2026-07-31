package ruiseki.okcore;

import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.core.ItemEnergyTestConfig;
import ruiseki.okcore.core.ItemFluidTestConfig;

public class Configs {

    public static void register(ConfigHandler configHandler) {
        configHandler.add(new ItemEnergyTestConfig());
        configHandler.add(new ItemFluidTestConfig());
    }
}
