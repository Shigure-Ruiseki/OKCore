package ruiseki.okcore.modcompat.common.fluid;

import net.minecraftforge.fluids.Fluid;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.FluidConfig;

public class FluidXpJuiceConfig extends FluidConfig {

    /**
     * The unique instance.
     */
    public static FluidXpJuiceConfig _instance;

    public FluidXpJuiceConfig() {
        super(
            OKCore._instance,
            true,
            "xpjuice",
            null,
            config -> new Fluid(config.getNamedId()).setLuminosity(10)
                .setDensity(800)
                .setViscosity(1500));
    }
}
