package ruiseki.okcore.config.extendedconfig;

import ruiseki.okcore.config.ConfigurableType;
import ruiseki.okcore.init.ModBase;

/**
 * Config for capabilities.
 * 
 * @author rubensworks
 * @see ExtendedConfig
 */
public abstract class CapabilityConfig<T> extends ExtendedConfig<CapabilityConfig<T>> {

    private final Class<T> type;

    /**
     * Make a new instance.
     * 
     * @param mod     The mod
     * @param enabled If this should is enabled.
     * @param namedId The unique name ID for the configurable.
     * @param comment The comment to add in the config file for this configurable.
     * @param type    The capability type.
     */
    public CapabilityConfig(ModBase mod, boolean enabled, String namedId, String comment, Class<T> type) {
        super(mod, enabled, namedId, comment, null);
        this.type = type;
    }

    @Override
    public String getUnlocalizedName() {
        return "capability." + getMod().getModId() + "." + getNamedId();
    }

    @Override
    public ConfigurableType getHolderType() {
        return ConfigurableType.CAPABILITY;
    }

    public Class<T> getType() {
        return type;
    }
}
