package ruiseki.okcore.config.configurable;

import net.minecraftforge.fluids.Fluid;

import ruiseki.okcore.config.extendedconfig.FluidConfig;

/**
 * Fluid that can hold ExtendedConfigs
 *
 * @author rubensworks
 *
 */
public abstract class ConfigurableFluid extends Fluid implements IConfigurable<FluidConfig> {

    protected FluidConfig eConfig = null;

    /**
     * Make a new fluid instance.
     *
     * @param eConfig Config for this blockState.
     */
    protected ConfigurableFluid(FluidConfig eConfig) {
        super(eConfig.getNamedId());
        this.setConfig(eConfig);
        this.setUnlocalizedName(eConfig.getUnlocalizedName());
    }

    private void setConfig(FluidConfig eConfig) {
        this.eConfig = eConfig;
    }

    @Override
    public FluidConfig getConfig() {
        return eConfig;
    }

}
