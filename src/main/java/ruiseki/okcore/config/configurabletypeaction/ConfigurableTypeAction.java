package ruiseki.okcore.config.configurabletypeaction;

import java.util.Objects;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.Level;

import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.config.UndisableableConfigException;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.registries.IForgeRegistry;
import ruiseki.okcore.registries.IForgeRegistryEntry;

/**
 * An action that is used to register Configurables.
 * Used inside of {@link ConfigHandler}.
 *
 * @author rubensworks
 * @param <C> The subclass of ExtendedConfig
 * @param <I> The instance type
 */
public abstract class ConfigurableTypeAction<C extends ExtendedConfig<C, I>, I> {

    /**
     * The common run method for all the subtypes of {@link ConfigurableTypeAction}.
     *
     * @param eConfig The config to be registered.
     * @param config  The target config file reference (matched with eConfig.getConfigLocation()).
     */
    public void commonRun(C eConfig, Configuration config) {
        if (eConfig.isDisableable()) {
            preRun(eConfig, config, true);
        }

        if (eConfig.isEnabled()) {
            postRun(eConfig, config);
        } else if (!eConfig.isDisableable()) {
            throw new UndisableableConfigException(eConfig);
        } else {
            onSkipRegistration(eConfig);
        }
    }

    protected void onSkipRegistration(C eConfig) {
        eConfig.getMod()
            .log(Level.TRACE, "Skipped registering " + eConfig.getNamedId());
    }

    /**
     * Logic that constructs or loads settings for eConfig from the target config file.
     *
     * @param eConfig configuration holder.
     * @param config  configuration from the target config file.
     * @param startup If this is currently being run at the mod startup.
     */
    public void preRun(C eConfig, Configuration config, boolean startup) {
        String category = eConfig.getHolderType()
            .getCategory();
        boolean enabled = config.get(category, eConfig.getNamedId() + ".enabled", true, eConfig.getComment())
            .getBoolean(true);

        if (!eConfig.isDisableable()) {
            enabled = true;
        }

        eConfig.setEnabled(enabled);
        if (enabled) {
            eConfig.save();
        }
    }

    /**
     * Logic to register the eConfig target into Forge Registries / Game engine.
     *
     * @param eConfig configuration holder.
     * @param config  configuration from the target config file.
     */
    public abstract void postRun(C eConfig, Configuration config);

    /**
     * Optional method that will be called during the initialization phase.
     *
     * @param config configuration holder.
     */
    public void polish(C config) {

    }

    /**
     * Register the {@link IForgeRegistryEntry}.
     *
     * @param instance The instance.
     * @param config   The corresponding config.
     * @param <T>      The type to register.
     */
    public static <T extends IForgeRegistryEntry<T>> void register(T instance, ExtendedConfig<?, ?> config) {
        register(instance, config, () -> {
            config.onForgeRegistered();
            return null;
        });
    }

    /**
     * Register the {@link IForgeRegistryEntry}.
     *
     * @param instance The instance.
     * @param config   The corresponding config.
     * @param callback A callback that will be called when the entry is registered.
     * @param <T>      The type to register.
     */
    public static <T extends IForgeRegistryEntry<T>> void register(T instance, ExtendedConfig<?, ?> config,
        @Nullable Callable<?> callback) {
        register(
            Objects.requireNonNull(
                (IForgeRegistry<T>) config.getRegistry(),
                "Tried registering a config for which no registry exists: " + config.getNamedId()),
            instance,
            config,
            callback);
    }

    /**
     * Register the {@link IForgeRegistryEntry}.
     *
     * @param registry The registry.
     * @param instance The instance.
     * @param config   The corresponding config.
     * @param <T>      The type to register.
     */
    public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, T instance,
        ExtendedConfig<?, ?> config) {
        register(registry, instance, config, null);
    }

    /**
     * Register the {@link IForgeRegistryEntry}.
     *
     * @param registry The registry.
     * @param instance The instance.
     * @param config   The corresponding config.
     * @param callback A callback that will be called when the entry is registered.
     * @param <T>      The type to register.
     */
    public static <T extends IForgeRegistryEntry<T>> void register(IForgeRegistry<T> registry, T instance,
        ExtendedConfig<?, ?> config, @Nullable Callable<?> callback) {
        if (instance.getRegistryName() == null) {
            instance.setRegistryName(
                new ResourceLocation(
                    config.getMod()
                        .getModId(),
                    config.getNamedId()));
        }
        config.getMod()
            .getConfigHandler()
            .registerToRegistry(registry, instance, callback);
    }
}
