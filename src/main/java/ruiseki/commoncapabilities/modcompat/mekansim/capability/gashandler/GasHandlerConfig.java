package ruiseki.commoncapabilities.modcompat.mekansim.capability.gashandler;

import mekanism.api.gas.IGasHandler;
import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the gas handler capability.
 *
 * @author ruiseki
 */
public class GasHandlerConfig extends CapabilityConfig<IGasHandler> {

    /**
     * The unique instance.
     */
    public static GasHandlerConfig _instance;

    @CapabilityInject(IGasHandler.class)
    public static Capability<IGasHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public GasHandlerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "gas_handler",
            "Capability for handling Mekanism gases",
            IGasHandler.class);
    }
}
