package ruiseki.okcore;

import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.core.item.ItemEnergyTestConfig;
import ruiseki.okcore.core.item.ItemFluidTestConfig;
import ruiseki.okcore.core.item.ItemInventoryTestConfig;
import ruiseki.okcore.core.item.gui.CapabilityTestGuiWrapperConfig;
import ruiseki.okcore.core.item.gui.ItemGuiTestConfig;
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

        configHandler.add(new ItemGuiTestConfig());
        configHandler.add(new CapabilityTestGuiWrapperConfig());
    }
}
