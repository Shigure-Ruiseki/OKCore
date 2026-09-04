package ruiseki.okcore.config.configurabletypeaction;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
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
        // Get property in config file and set comment
        Property property = config.get(
            eConfig.getHolderType().getCategory(),
            eConfig.getNamedId(),
            eConfig.isEnabled());
        property.setRequiresMcRestart(true);
        property.comment = eConfig.getComment();

        if (startup) {
            // Update the ID, it could've changed
            eConfig.setEnabled(property.getBoolean(true));
        }
    }

    @Override
    public void postRun(FluidConfig eConfig, Configuration config) {
        // Save the config inside the correct element
        eConfig.save();

        Fluid fluid = eConfig.getInstance();
        if (fluid != null) {
            fluid.setUnlocalizedName(eConfig.getUnlocalizedName());
            FluidRegistry.registerFluid(fluid);
        }
    }
}
