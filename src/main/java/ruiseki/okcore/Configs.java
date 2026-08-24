package ruiseki.okcore;

import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.core.ItemEnergyTestConfig;
import ruiseki.okcore.core.ItemFluidTestConfig;
import ruiseki.okcore.core.ItemInventoryTestConfig;
import ruiseki.okcore.energy.capability.EnergyStorageConfig;
import ruiseki.okcore.fluid.capability.FluidHandlerConfig;
import ruiseki.okcore.fluid.capability.FluidHandlerItemCapacityConfig;
import ruiseki.okcore.fluid.capability.FluidHandlerItemConfig;
import ruiseki.okcore.item.capability.ItemHandlerConfig;

public class Configs {

    public static void register(ConfigHandler configHandler) {
        // Capabilities
        configHandler.add(new FluidHandlerConfig());
        configHandler.add(new FluidHandlerItemConfig());
        configHandler.add(new FluidHandlerItemCapacityConfig());
        configHandler.add(new EnergyStorageConfig());
        configHandler.add(new ItemHandlerConfig());

        configHandler.add(new ItemEnergyTestConfig());
        configHandler.add(new ItemFluidTestConfig());
        configHandler.add(new ItemInventoryTestConfig());
    }
}
