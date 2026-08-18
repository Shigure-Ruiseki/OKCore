package ruiseki.okcore.config.configurabletypeaction;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import ruiseki.okcore.config.extendedconfig.FluidConfig;

/**
 * The action used for {@link FluidConfig}.
 *
 * @author rubensworks
 * @see ConfigurableTypeAction
 */
public class FluidAction extends ConfigurableTypeAction<FluidConfig, Fluid> {

    @Override
    public void preRun(FluidConfig eConfig, Configuration config, boolean startup) {

    }

    @Override
    public void postRun(FluidConfig eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        // Register
        FluidRegistry.registerFluid(eConfig.getInstance());
    }
}
