package ruiseki.okcore.config.configurable;

import net.minecraftforge.fluids.Fluid;

import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.config.extendedconfig.FluidConfig;

/**
 * Fluid that can hold ExtendedConfigs
 * 
 * @author rubensworks
 *
 */
public abstract class ConfigurableFluid extends Fluid implements IConfigurable {

    @SuppressWarnings("rawtypes")
    protected ExtendedConfig eConfig = null;

    /**
     * Make a new fluid instance.
     * 
     * @param eConfig Config for this blockState.
     */
    @SuppressWarnings({ "rawtypes" })
    protected ConfigurableFluid(ExtendedConfig<FluidConfig> eConfig) {
        super(eConfig.getNamedId());
        this.setConfig(eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
    }

    @SuppressWarnings("rawtypes")
    private void setConfig(ExtendedConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public ExtendedConfig<?> getConfig() {
        return eConfig;
    }

}
