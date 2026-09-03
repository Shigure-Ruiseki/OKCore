package ruiseki.okcore.config;

import java.io.File;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Data;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.network.packet.PacketSyncConfig;
import ruiseki.okcore.registries.IForgeRegistry;
import ruiseki.okcore.registries.IForgeRegistryEntry;
import ruiseki.okcore.registries.RegistryEvent;

@SuppressWarnings("rawtypes")
@Data
public class ConfigHandler extends LinkedHashSet<ExtendedConfig<?, ?>> {

    private final ModBase mod;
    private final Map<ConfigLocation, Configuration> configs = new EnumMap<>(ConfigLocation.class);

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

    static final Map<String, ConfigProperty> syncedElements = new Object2ObjectOpenHashMap<>();
    private static boolean hasSyncedValues = false;

    public ConfigHandler(ModBase mod) {
        this.mod = mod;
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @Override
    public boolean add(ExtendedConfig<?, ?> e) {
        if (configDictionary.containsKey(e.getNamedId())) {
            mod.log(
                Level.WARN,
                "Config with ID " + e.getNamedId() + " already exists! Skipping duplicate registration.");
            return false;
        }
        addToConfigDictionary(e);
        return super.add(e);
    }

    public void addToConfigDictionary(ExtendedConfig<?, ?> e) {
        configDictionary.put(e.getNamedId(), e);
    }

    public void handle(FMLPreInitializationEvent event) {
        if (configs.isEmpty()) {
            File baseConfigDir = new File(event.getModConfigurationDirectory(), mod.getModId());

            for (ConfigLocation location : ConfigLocation.values()) {
                File configFile = new File(baseConfigDir, mod.getModId() + "-" + location.extension() + ".cfg");
                Configuration config = new Configuration(configFile);
                config.load();
                configs.put(location, config);
            }
        }

        loadConfig();
    }

    public void addCategory(String category) {
        categories.add(category);
    }

    @SuppressWarnings("unchecked")
    public void loadConfig() {
        enabledConfigs.clear();
        for (ExtendedConfig<?, ?> eConfig : this) {
            try {
                addCategory(
                    eConfig.getHolderType()
                        .getCategory());
                if (!eConfig.isHardDisabled()) {
                    for (ConfigProperty configProperty : eConfig.configProperties) {
                        categories.add(configProperty.getCategory());

                        ConfigLocation loc = configProperty.getLocation();
                        Configuration targetConfig = getConfig(loc);

                        configProperty.save(targetConfig);

                        if (configProperty.isCommandable()) {
                            commandableProperties.put(configProperty.getName(), configProperty);
                        }
                        if (loc.isSyncToServer()) {
                            syncedElements.put(configProperty.getName(), configProperty);
                        }
                    }

                    Configuration defaultConfig = getConfig(ConfigLocation.COMMON);
                    eConfig.getHolderType()
                        .getElementTypeAction()
                        .commonRun(eConfig, defaultConfig);

                    if (eConfig.isEnabled()) {
                        eConfig.onRegistered();
                        mod.log(Level.TRACE, "Registered " + eConfig.getNamedId());
                        processedConfigs.add(eConfig);

                        mod.addInitListeners(new ConfigInitListener(eConfig));
                        enabledConfigs.add((Class<? extends ExtendedConfig<?, ?>>) eConfig.getClass());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        this.removeAll(this);
        saveAllConfigs();
    }

    public void polishConfigs() {
        for (ExtendedConfig<?, ?> eConfig : processedConfigs) {
            ConfigurableType type = eConfig.getHolderType();
            type.getElementTypeAction()
                .polish(eConfig);
        }
    }

    @SuppressWarnings("unchecked")
    public void syncProcessedConfigs() {
        for (ExtendedConfig<?, ?> eConfig : processedConfigs) {
            for (ConfigProperty configProperty : eConfig.configProperties) {
                ConfigLocation loc = configProperty.getLocation();
                Configuration targetConfig = getConfig(loc);

                configProperty.save(targetConfig, false);

                ConfigurableType type = eConfig.getHolderType();
                type.getElementTypeAction()
                    .preRun(eConfig, targetConfig, false);
            }
        }

        saveAllConfigs();
    }

    public Configuration getConfig(ConfigLocation location) {
        return configs.get(location);
    }

    /**
     * Phương thức tương thích ngược - trả về file COMMON config.
     */
    public Configuration getConfig() {
        return getConfig(ConfigLocation.COMMON);
    }

    public void saveAllConfigs() {
        for (Configuration config : configs.values()) {
            config.save();
        }
    }

    public Map<String, ExtendedConfig<?, ?>> getDictionary() {
        return configDictionary;
    }

    public static class ConfigInitListener implements IInitListener {

        private ExtendedConfig<?, ?> config;

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

    public boolean isConfigEnabled(Class<? extends ExtendedConfig<?, ?>> config) {
        return enabledConfigs.contains(config);
    }

    public <V extends IForgeRegistryEntry<V>> void registerToRegistry(IForgeRegistry<V> registry,
        IForgeRegistryEntry<V> entry, @Nullable Callable<?> callback) {
        if (this.registryEventPassed) {
            throw new IllegalStateException(
                String.format("Tried registering %s after its registration event.", entry.getRegistryName()));
        }
        registryEntriesHolder.put(registry.getRegistrySuperType(), Pair.of(entry, callback));
    }

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

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.player instanceof EntityPlayerMP playerMP)) return;
        MinecraftServer server = MinecraftServer.getServer();
        if (server.isSinglePlayer() && !((IntegratedServer) server).getPublic()) {
            return;
        }
        OKCore._instance.getPacketHandler()
            .sendToPlayer(new PacketSyncConfig(syncedElements), playerMP);
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (!hasSyncedValues) return;
        hasSyncedValues = false;
        for (ConfigProperty element : syncedElements.values()) {
            element.restoreLocalValue();
        }
    }

    public static void onSync(PacketSyncConfig packet) {
        for (Map.Entry<String, String> entry : packet.getSyncedProperties()
            .entrySet()) {
            ConfigProperty element = syncedElements.get(entry.getKey());
            if (element != null) {
                try {
                    hasSyncedValues = true;
                    element.setSyncedValue(entry.getValue());
                } catch (Exception e) {
                    OKCore.okLog(Level.ERROR, "Failed to sync element " + element.getName(), e);
                }
            }
        }
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
