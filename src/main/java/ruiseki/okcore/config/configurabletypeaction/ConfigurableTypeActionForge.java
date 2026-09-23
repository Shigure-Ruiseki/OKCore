package ruiseki.okcore.config.configurabletypeaction;

import net.minecraftforge.common.config.Configuration;

import ruiseki.okcore.config.extendedconfig.ExtendedConfigForge;
import ruiseki.okcore.registries.IForgeRegistryEntry;

/**
 * The action used for {@link ExtendedConfigForge}.
 *
 * @author rubensworks
 * @see ConfigurableTypeAction
 *
 * @param <C> The subclass of ExtendedConfigForge.
 * @param <I> The instance corresponding to this config, which extends IForgeRegistryEntry.
 */
public abstract class ConfigurableTypeActionForge<C extends ExtendedConfigForge<C, I>, I extends IForgeRegistryEntry<?>>
    extends ConfigurableTypeAction<C, I> {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void postRun(C eConfig, Configuration config) {
        if (eConfig.isEnabled()) {
            I instance = eConfig.getInstance();
            if (instance != null) {
                register((IForgeRegistryEntry) instance, eConfig);
            }
        }
    }
}
