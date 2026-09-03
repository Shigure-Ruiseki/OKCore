package ruiseki.okcore.client.gui.config;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.IConfigElement;
import ruiseki.okcore.config.ConfigLocation;
import ruiseki.okcore.init.ModBase;

public abstract class GuiConfigOverviewBase extends GuiConfig {

    public GuiConfigOverviewBase(ModBase mod, GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(mod), mod.getModId(), false, false, mod.getModName() + " Configuration");
    }

    public abstract ModBase getMod();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static List<IConfigElement> getConfigElements(ModBase mod) {
        List<IConfigElement> list = new ArrayList<>();

        for (ConfigLocation location : ConfigLocation.values()) {
            String locationName = location.name()
                .toUpperCase();

            list.add(
                new DummyConfigElement.DummyCategoryElement(
                    locationName,
                    "config." + mod.getModId()
                        + "."
                        + location.name()
                            .toLowerCase(),
                    LocationCategoryEntry.class));
        }
        return list;
    }

    public static class LocationCategoryEntry extends GuiConfigEntries.CategoryEntry {

        public LocationCategoryEntry(GuiConfig config, GuiConfigEntries entries, IConfigElement element) {
            super(config, entries, element);
        }

        @Override
        protected GuiScreen buildChildScreen() {
            ModBase mod = ((GuiConfigOverviewBase) this.owningScreen).getMod();

            ConfigLocation location = ConfigLocation.valueOf(this.configElement.getName());
            Configuration targetConfig = mod.getConfigHandler()
                .getConfig(location);

            List<IConfigElement> childElements = new ArrayList<>();

            for (String categoryName : targetConfig.getCategoryNames()) {
                if (!categoryName.contains(Configuration.CATEGORY_SPLITTER)) {
                    childElements.add(new ConfigElement(targetConfig.getCategory(categoryName)));
                }
            }

            return new GuiConfig(
                this.owningScreen,
                childElements,
                this.owningScreen.modID,
                location.name(),
                this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart,
                this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart,
                GuiConfig.getAbridgedConfigPath(targetConfig.toString()));
        }
    }
}
