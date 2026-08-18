package ruiseki.okcore.config;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.Data;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.registries.IForgeRegistry;
import ruiseki.okcore.registries.IForgeRegistryEntry;
import ruiseki.okcore.registries.RegistryEvent;

/**
 * Create config file and register items and blocks from the given ExtendedConfigs.
 *
 * @author rubensworks
 *
 */
@SuppressWarnings("rawtypes")
@Data
public class ConfigHandler extends LinkedHashSet<ExtendedConfig<?, ?>> {

    private final ModBase mod;
    private Configuration config;
    private final LinkedHashSet<ExtendedConfig<?, ?>> processedConfigs = new LinkedHashSet<ExtendedConfig<?, ?>>();
    private final Map<String, ExtendedConfig<?, ?>> configDictionary = Maps.newHashMap();
    private final Set<String> categories = Sets.newHashSet();
    private final Map<String, ConfigProperty> commandableProperties = Maps.newHashMap();
    private final Multimap<Class<?>, Pair<IForgeRegistryEntry<?>, Callable<?>>> registryEntriesHolder = Multimaps
        .newListMultimap(Maps.newIdentityHashMap(), new Supplier<List<Pair<IForgeRegistryEntry<?>, Callable<?>>>>() {

            @Override
            public List<Pair<IForgeRegistryEntry<?>, Callable<?>>> get() {
                return Lists.newArrayList();
            }
        });
    private boolean registryEventPassed = false;
    private final Set<Class<? extends ExtendedConfig<?, ?>>> enabledConfigs = Sets.newIdentityHashSet();

    public ConfigHandler(ModBase mod) {
        this.mod = mod;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public boolean add(ExtendedConfig<?, ?> e) {
        addToConfigDictionary(e);
        return super.add(e);
    }

    public void addToConfigDictionary(ExtendedConfig<?, ?> e) {
        configDictionary.put(e.getNamedId(), e);
    }

    /**
     * Iterate over the given ExtendedConfigs to read/write the config and register the given elements
     * This also sets the config of this instance.
     *
     * @param event the event from the init methods
     */
    public void handle(FMLPreInitializationEvent event) {
        if (getConfig() == null) {
            // You will be able to find the config file in .minecraft/config/ and it will be named EvilCraft.cfg
            // here our Configuration has been instantiated, and saved under the name "config"
            // If the file doesn't already exist, it will be created.
            Configuration config = new Configuration(event.getSuggestedConfigurationFile());
            setConfig(config);

            // Loading the configuration from its file
            config.load();
        }

        loadConfig();
    }

    /**
     * Add a config category.
     *
     * @param category The category to add.
     */
    public void addCategory(String category) {
        categories.add(category);
    }

    /**
     * Iterate over the given ExtendedConfigs to read/write the config and register the given elements.
     */
    @SuppressWarnings("unchecked")
    public void loadConfig() {
        enabledConfigs.clear();
        for (ExtendedConfig<?, ?> eConfig : this) {
            try {
                addCategory(
                    eConfig.getHolderType()
                        .getCategory());
                if (!eConfig.isHardDisabled()) {
                    // Save additional properties
                    for (ConfigProperty configProperty : eConfig.configProperties) {
                        categories.add(configProperty.getCategory());
                        configProperty.save(config);
                        if (configProperty.isCommandable()) {
                            commandableProperties.put(configProperty.getName(), configProperty);
                        }
                    }

                    // Register the element depending on the type.
                    eConfig.getHolderType()
                        .getElementTypeAction()
                        .commonRun(eConfig, config);

                    if (eConfig.isEnabled()) {
                        // Call the listener
                        eConfig.onRegistered();

                        mod.log(Level.TRACE, "Registered " + eConfig.getNamedId());
                        processedConfigs.add(eConfig);

                        // Register as init listener.
                        mod.addInitListeners(new ConfigInitListener(eConfig));
                        enabledConfigs.add((Class<? extends ExtendedConfig<?, ?>>) eConfig.getClass());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Forge seems to silently ignore these errors, so let's print them manually.
                throw e;
            }
        }

        // Empty the configs so they won't be loaded again later
        this.removeAll(this);

        // Saving the configuration to its file
        config.save();
    }

    /**
     * Polish the enabled configs during the initialization phase.
     */
    @SuppressWarnings("unchecked")
    public void polishConfigs() {
        for (ExtendedConfig<?, ?> eConfig : processedConfigs) {
            ConfigurableType type = eConfig.getHolderType();
            type.getElementTypeAction()
                .polish(eConfig);
        }
    }

    /**
     * Sync the config values that were already loaded.
     * This will update the values in-game and in the config file.
     */
    @SuppressWarnings("unchecked")
    public void syncProcessedConfigs() {
        for (ExtendedConfig<?, ?> eConfig : processedConfigs) {
            // Re-save additional properties
            for (ConfigProperty configProperty : eConfig.configProperties) {
                configProperty.save(config, false);
            }

            // Register the element depending on the type.
            ConfigurableType type = eConfig.getHolderType();
            type.getElementTypeAction()
                .preRun(eConfig, config, false);
        }

        // Update the config file.
        getConfig().save();
    }

    /**
     * @return the config
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(Configuration config) {
        this.config = config;
    }

    /**
     * Get the map of config nameid to config.
     *
     * @return The dictionary.
     */
    public Map<String, ExtendedConfig<?, ?>> getDictionary() {
        return configDictionary;
    }

    /**
     * Init listener for configs.
     *
     * @author rubensworks
     *
     */
    public static class ConfigInitListener implements IInitListener {

        private ExtendedConfig<?, ?> config;

        /**
         * Make a new instance.
         *
         * @param config The config.
         */
        public ConfigInitListener(ExtendedConfig<?, ?> config) {
            this.config = config;
        }

        @Override
        public void onInit(IInitListener.Step step) {
            config.onInit(step);
            if (step == IInitListener.Step.POSTINIT) {
                for (ConfigProperty property : config.configProperties) {
                    IChangedCallback changedCallback = property.getCallback()
                        .getChangedCallback();
                    if (changedCallback != null) {
                        changedCallback.onRegisteredPostInit(property.getValue());
                    }
                }
            }
        }

    }

    /**
     * ExtendedConfig#isEnabled()
     *
     * @param config The config to check.
     * @return If the given config is enabled.
     */
    public boolean isConfigEnabled(Class<? extends ExtendedConfig<?, ?>> config) {
        return enabledConfigs.contains(config);
    }

    /**
     * Register the given entry to the given registry.
     * This method will safely wait until the correct registry event for registering the entry.
     *
     * @param registry The registry.
     * @param entry    The entry.
     * @param callback A callback that will be called when the entry is registered.
     * @param <V>      The entry type.
     */
    public <V extends IForgeRegistryEntry<V>> void registerToRegistry(IForgeRegistry<V> registry,
        IForgeRegistryEntry<V> entry, @Nullable Callable<?> callback) {
        if (this.registryEventPassed) {
            throw new IllegalStateException(
                String.format("Tried registering %s after its registration event.", entry.getRegistryName()));
        }
        registryEntriesHolder.put(registry.getRegistrySuperType(), Pair.of(entry, callback));
    }

    /**
     * Register the given entry to the given registry.
     * This method will safely wait until the correct registry event for registering the entry.
     *
     * @param registry The registry.
     * @param entry    The entry.
     * @param <V>      The entry type.
     */
    public <V extends IForgeRegistryEntry<V>> void registerToRegistry(IForgeRegistry<V> registry,
        IForgeRegistryEntry<V> entry) {
        registerToRegistry(registry, entry, null);
    }

    @SubscribeEvent
    public void onRegistryEvent(RegistryEvent.Register event) {
        this.registryEventPassed = true;
        IForgeRegistry registry = event.getRegistry();
        registryEntriesHolder.get(registry.getRegistrySuperType())
            .forEach((pair) -> {
                registry.register(pair.getLeft());
                try {
                    if (pair.getRight() != null) {
                        pair.getRight()
                            .call();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ConfigHandler && ((ConfigHandler) o).getMod()
            .equals(this.getMod());
    }

    @Override
    public int hashCode() {
        return 1 + this.getMod()
            .hashCode();
    }

}
