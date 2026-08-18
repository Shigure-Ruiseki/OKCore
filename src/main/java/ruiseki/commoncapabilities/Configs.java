package ruiseki.commoncapabilities;

import ruiseki.commoncapabilities.capability.ingredient.storage.IngredientComponentStorageHandlerConfig;
import ruiseki.commoncapabilities.capability.inventorystate.InventoryStateConfig;
import ruiseki.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.commoncapabilities.capability.temperature.TemperatureConfig;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.commoncapabilities.capability.wrench.WrenchConfig;
import ruiseki.commoncapabilities.modcompat.mekansim.MekanismConfigs;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.enums.Mods;

public class Configs {

    public static void register(ConfigHandler configHandler) {
        configHandler.add(new WorkerConfig());
        configHandler.add(new WrenchConfig());
        configHandler.add(new TemperatureConfig());
        configHandler.add(new InventoryStateConfig());
        configHandler.add(new SlotlessItemHandlerConfig());
        configHandler.add(new RecipeHandlerConfig());
        configHandler.add(new IngredientComponentStorageHandlerConfig());

        if (Mods.Mekanism.isModLoaded()) {
            MekanismConfigs.register(configHandler);
        }
    }
}
