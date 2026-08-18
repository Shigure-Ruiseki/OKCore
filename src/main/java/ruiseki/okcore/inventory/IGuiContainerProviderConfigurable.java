package ruiseki.okcore.inventory;

import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * Interface for configurables providing gui-containers.
 *
 * @author rubensworks
 *
 */
public interface IGuiContainerProviderConfigurable extends IGuiContainerProvider {

    /**
     * @return The configurable config.
     */
    public ExtendedConfig<?, ?> getConfig();

}
