package ruiseki.okcore.config.extendedconfig;

import java.util.function.Function;

import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.registries.IForgeRegistry;
import ruiseki.okcore.tag.ResourceKey;

/**
 * An extended config for instances that are to be registered in a Forge registry.
 * 
 * @param <C> Class of the extension of ExtendedConfig
 * @param <I> The instance corresponding to this config.
 */
public abstract class ExtendedConfigForge<C extends ExtendedConfig<C, I>, I> extends ExtendedConfig<C, I> {

    /**
     * Create a new config
     *
     * @param mod                The mod instance.
     * @param enable             Whether this config is enabled.
     * @param namedId            A unique name id.
     * @param comment            Comment for config file.
     * @param elementConstructor The element constructor.
     */
    public ExtendedConfigForge(ModBase mod, boolean enable, String namedId, String comment,
        Function<C, I> elementConstructor) {
        super(mod, enable, namedId, comment, elementConstructor);
    }

    /**
     * @return The registry in which this should be registered.
     */
    public abstract IForgeRegistry<? super I> getRegistry();

    public ResourceKey<I> getResourceKey() {
        return ResourceKey.create(
            ResourceKey.createRegistryKey(getRegistry().getRegistryName()),
            new ResourceLocation(getMod().getModId(), getNamedId()));
    }

}
