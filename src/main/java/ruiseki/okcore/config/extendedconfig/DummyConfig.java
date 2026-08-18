package ruiseki.okcore.config.extendedconfig;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Dummy config.
 *
 * @author rubensworks
 * @see ExtendedConfig
 *
 */
public class DummyConfig extends ExtendedConfig<DummyConfig, Void> {

    /**
     * Make a new instance.
     *
     * @param mod     The mod instance.
     * @param enabled If this should is enabled.
     * @param namedId The unique name ID for the configurable.
     * @param comment The comment to add in the config file for this configurable.
     */
    public DummyConfig(ModBase mod, boolean enabled, String namedId, String comment) {
        super(mod, enabled, namedId, comment, (c) -> null);
    }

    @Deprecated
    public DummyConfig(ModBase mod, boolean enabled, String namedId, String comment, Class empty) {
        super(mod, enabled, namedId, comment, (c) -> null);
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.DUMMY;
    }

    @Override
    public String getUnlocalizedName() {
        return getNamedId();
    }

}
