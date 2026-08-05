package ruiseki.commoncapabilities.capability.wrench;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.api.capability.wrench.IWrench;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the wrench capability.
 * 
 * @author rubensworks
 *
 */
public class WrenchConfig extends CapabilityConfig<IWrench> {

    /**
     * The unique instance.
     */
    public static WrenchConfig _instance;

    @CapabilityInject(IWrench.class)
    public static Capability<IWrench> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public WrenchConfig() {
        super(CommonCapabilities._instance, true, "wrench", "Indicates if something is a wrench", IWrench.class);
    }
}
