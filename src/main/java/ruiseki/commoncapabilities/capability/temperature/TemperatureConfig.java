package ruiseki.commoncapabilities.capability.temperature;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.commoncapabilities.api.capability.temperature.ITemperature;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the temperature capability.
 * 
 * @author rubensworks
 *
 */
public class TemperatureConfig extends CapabilityConfig<ITemperature> {

    /**
     * The unique instance.
     */
    public static TemperatureConfig _instance;

    @CapabilityInject(ITemperature.class)
    public static Capability<ITemperature> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public TemperatureConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "temperature",
            "Indicates if something has a temperature",
            ITemperature.class);
    }
}
