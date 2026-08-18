package ruiseki.commoncapabilities.modcompat.mekansim;

import ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler.GasHandlerConfig;
import ruiseki.commoncapabilities.modcompat.mekansim.item.ItemGasTestConfig;
import ruiseki.okcore.config.ConfigHandler;

public class MekanismConfigs {

    public static void register(ConfigHandler configHandler) {
        // Capabilities
        configHandler.add(new GasHandlerConfig());

        // Item
        configHandler.add(new ItemGasTestConfig());
    }
}
