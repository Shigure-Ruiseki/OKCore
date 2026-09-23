package ruiseki.okcore.config.configurabletypeaction;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import ruiseki.okcore.client.gui.GuiType;
import ruiseki.okcore.config.extendedconfig.GuiConfig;
import ruiseki.okcore.inventory.container.ContainerExtended;

/**
 * The action used for {@link GuiConfig}.
 *
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class GuiAction<T extends ContainerExtended> extends ConfigurableTypeActionForge<GuiConfig<T>, GuiType<T>> {

    @Override
    public void preRun(GuiConfig<T> eConfig, Configuration config, boolean startup) {
        Property property = config.get(
            eConfig.getHolderType()
                .getCategory(),
            eConfig.getNamedId(),
            eConfig.isEnabled());
        property.setRequiresMcRestart(true);
        property.comment = eConfig.getComment();

        if (startup) {
            eConfig.setEnabled(property.getBoolean(true));
        }
    }

    @Override
    public void postRun(GuiConfig<T> eConfig, Configuration config) {
        eConfig.save();
        super.postRun(eConfig, config);
    }
}
