package ruiseki.okcore.config.configurable;

import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * Interface for all elements that are configurable.
 * Each type has one unique {@link ExtendedConfig} that must be registered inside the
 * {@link ruiseki.okcore.config.ConfigHandler}.
 * 
 * @author rubensworks
 *
 */
public interface IConfigurable {

    public ExtendedConfig<?> getConfig();

}
