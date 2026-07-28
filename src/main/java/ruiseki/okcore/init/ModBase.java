package ruiseki.okcore.init;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.brigadier.BrigadierApi;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import lombok.Data;
import ruiseki.okcore.client.gui.GuiHandler;
import ruiseki.okcore.client.key.IKeyRegistry;
import ruiseki.okcore.client.key.KeyRegistry;
import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.command.CommandVersion;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.LoggerHelpers;
import ruiseki.okcore.helper.VersionHelpers;
import ruiseki.okcore.network.PacketHandler;
import ruiseki.okcore.persist.world.WorldStorage;
import ruiseki.okcore.proxy.ICommonProxy;

/**
 * Base class for mods which adds a few convenience methods.
 * Dont forget to call the supers for the init events.
 *
 * @author rubensworks
 */
@Data
public abstract class ModBase {

    public static final EnumReferenceKey<String> REFKEY_MOD_VERSION = EnumReferenceKey
        .create("mod_version", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_GUI = EnumReferenceKey
        .create("texture_path_gui", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_MODELS = EnumReferenceKey
        .create("texture_path_models", String.class);
    public static final EnumReferenceKey<String> REFKEY_TEXTURE_PATH_SKINS = EnumReferenceKey
        .create("texture_path_skins", String.class);
    public static final EnumReferenceKey<Boolean> REFKEY_RETROGEN = EnumReferenceKey.create("retrogen", Boolean.class);

    public static final EnumReferenceKey<Boolean> REFKEY_VERSION_CHECKER = EnumReferenceKey
        .create("version_check_enable", Boolean.class);
    public static final EnumReferenceKey<String> REFKEY_VERSION_CHECKER_URL = EnumReferenceKey
        .create("version_check_url", String.class);
    public static final EnumReferenceKey<Map> REFKEY_VERSION_CHECKER_DOWNLOADS = EnumReferenceKey
        .create("version_check_downloads", Map.class);
    public static final EnumReferenceKey<String> REFKEY_VERSION_CHECKER_LATEST = EnumReferenceKey
        .create("version_check_latest", String.class);
    public static final EnumReferenceKey<String> REFKEY_VERSION_CHECKER_STATUS = EnumReferenceKey
        .create("version_check_status", String.class);

    public static final EnumReferenceKey<Boolean> REFKEY_DEBUGCONFIG = EnumReferenceKey
        .create("debug_config", Boolean.class);

    private final String modId, modName;
    private final LoggerHelpers loggerHelper;
    private final Set<IInitListener> initListeners;
    private final ConfigHandler configHandler;
    private final Map<EnumReferenceKey, Object> genericReference = Maps.newHashMap();
    private final List<WorldStorage> worldStorages = Lists.newLinkedList();
    private final GuiHandler guiHandler;
    private LiteralArgumentBuilder<ICommandSender> baseCommand;
    private final RegistryManager registryManager;
    private final IKeyRegistry keyRegistry;
    private final PacketHandler packetHandler;
    private final ModuleManager moduleManager;
    private final Debug debug;

    private CreativeTabs defaultCreativeTab = null;
    private File configFolder = null;

    public ModBase(String modId, String modName) {
        this.modId = modId;
        this.modName = modName;
        this.loggerHelper = constructLoggerHelper();
        this.initListeners = Sets.newHashSet();
        this.configHandler = constructConfigHandler();
        this.guiHandler = constructGuiHandler();
        this.registryManager = constructRegistryManager();
        this.keyRegistry = new KeyRegistry();
        this.packetHandler = constructPacketHandler();
        this.moduleManager = constructModuleManager();
        this.debug = new Debug(this);

        populateDefaultGenericReferences();
    }

    protected LoggerHelpers constructLoggerHelper() {
        return new LoggerHelpers(this.modName);
    }

    protected ConfigHandler constructConfigHandler() {
        return new ConfigHandler(this);
    }

    protected GuiHandler constructGuiHandler() {
        return new GuiHandler(this);
    }

    protected RegistryManager constructRegistryManager() {
        return new RegistryManager();
    }

    protected PacketHandler constructPacketHandler() {
        return new PacketHandler(this);
    }

    protected LiteralArgumentBuilder<ICommandSender> constructBaseCommand(MinecraftServer server) {
        return new CommandMod(this).make()
            .then(new CommandVersion(this).make());
    }

    protected ModuleManager constructModuleManager() {
        return new ModuleManager(this);
    }

    /**
     * Save a mod value.
     *
     * @param key   The key.
     * @param value The value.
     * @param <T>   The value type.
     */
    public <T> void putGenericReference(EnumReferenceKey<T> key, T value) {
        genericReference.put(key, value);
    }

    private void populateDefaultGenericReferences() {
        putGenericReference(REFKEY_TEXTURE_PATH_GUI, "textures/gui/");
        putGenericReference(REFKEY_TEXTURE_PATH_MODELS, "textures/models/");
        putGenericReference(REFKEY_TEXTURE_PATH_SKINS, "textures/skins/");
        putGenericReference(REFKEY_RETROGEN, false);

        putGenericReference(REFKEY_VERSION_CHECKER, false);
        putGenericReference(REFKEY_VERSION_CHECKER_URL, "");
        putGenericReference(REFKEY_VERSION_CHECKER_LATEST, "0.0.0.0");
        putGenericReference(REFKEY_VERSION_CHECKER_STATUS, VersionHelpers.STATUS_UNKNOWN);
        putGenericReference(REFKEY_VERSION_CHECKER_DOWNLOADS, Maps.<String, String>newHashMap());

        putGenericReference(REFKEY_DEBUGCONFIG, false);
    }

    /**
     * Get the value for a generic reference key.
     * The default keys can be found in {@link ModBase}.
     *
     * @param key The key of a value.
     * @param <T> The type of value.
     * @return The value for the given key.
     */
    @SuppressWarnings("unchecked")
    public <T> T getReferenceValue(EnumReferenceKey<T> key) {
        if (!genericReference.containsKey(key))
            throw new IllegalArgumentException("Could not find " + key + " as generic reference item.");
        return (T) genericReference.get(key);
    }

    /**
     * Log a new info message for this mod.
     *
     * @param message The message to show.
     */
    public void log(String message) {
        log(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     */
    public void log(Level level, String message) {
        loggerHelper.log(level, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     * @param params  Parameters to replace in the message.
     */
    public void log(Level level, String message, Object... params) {
        loggerHelper.log(level, message, params);
    }

    /**
     * Register a new mod Module.
     */
    public void registerModule(ModModuleBase module) {
        synchronized (moduleManager) {
            moduleManager.register(module);
        }
    }

    public void registerSubCommand(LiteralArgumentBuilder<ICommandSender> parent) {

    }

    /**
     * Register a new init listener.
     *
     * @param initListener The init listener.
     */
    public void addInitListeners(IInitListener initListener) {
        synchronized (initListeners) {
            initListeners.add(initListener);
        }
    }

    /**
     * Get the init-listeners on a thread-safe way;
     *
     * @return A copy of the init listeners list.
     */
    private Set<IInitListener> getSafeInitListeners() {
        Set<IInitListener> clonedInitListeners;
        synchronized (initListeners) {
            clonedInitListeners = Sets.newHashSet(initListeners);
        }
        return clonedInitListeners;
    }

    /**
     * Call the init-listeners for the given step.
     *
     * @param step The step of initialization.
     */
    protected void callInitStepListeners(IInitListener.Step step) {
        for (IInitListener initListener : getSafeInitListeners()) {
            initListener.onInit(step);
        }
    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     *
     * @param event The pre-init event.
     */
    public void preInit(FMLPreInitializationEvent event) {
        log(Level.TRACE, "preInit()");
        moduleManager.preInit(event);

        if (getConfigFolder() == null) {
            // Determine config folder.
            String rootFolderName = event.getModConfigurationDirectory() + "/" + getModId();
            File configFolder = new File(rootFolderName);
            setConfigFolder(configFolder);
        }

        // Register configs and start with loading the general configs
        onGeneralConfigsRegister(getConfigHandler());
        getConfigHandler().handle(event);
        onMainConfigsRegister(getConfigHandler());

        // Call init listeners
        callInitStepListeners(IInitListener.Step.PREINIT);

        // Run debugging tools
        if (getReferenceValue(REFKEY_DEBUGCONFIG)) {
            getDebug().checkPreConfigurables(getConfigHandler());
        }

        // Load the rest of the configs and run the ConfigHandler to make/read the config and fill in the game registry
        getConfigHandler().handle(event);

        // Run debugging tools
        if (getReferenceValue(REFKEY_DEBUGCONFIG)) {
            getDebug().checkPostConfigurables();
        }

        // Register events
        ICommonProxy proxy = getProxy();
        if (proxy != null) {
            proxy.registerEventHooks();
        }
        moduleManager.proxyPreInit();
    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     *
     * @param event The init event.
     */
    public void init(FMLInitializationEvent event) {
        log(Level.TRACE, "init()");
        moduleManager.init(event);

        // Gui Handlers
        NetworkRegistry.INSTANCE.registerGuiHandler(getModId(), getGuiHandler());

        // Initialize the creative tab
        getDefaultCreativeTab();

        // Polish the enabled configs.
        getConfigHandler().polishConfigs();

        // Call init listeners
        callInitStepListeners(IInitListener.Step.INIT);

        // Register proxy related things.
        ICommonProxy proxy = getProxy();
        if (proxy != null) {
            proxy.registerRenderers();
            proxy.registerKeyBindings(getKeyRegistry());
            getPacketHandler().init();
            proxy.registerPacketHandlers(getPacketHandler());
            proxy.registerTickHandlers();
        }
        moduleManager.proxyInit();
    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     *
     * @param event The post-init event.
     */
    public void postInit(FMLPostInitializationEvent event) {
        log(Level.TRACE, "postInit()");
        moduleManager.postInit(event);

        // Call init listeners
        callInitStepListeners(IInitListener.Step.POSTINIT);
        if (this.getReferenceValue(REFKEY_VERSION_CHECKER) && !this.getReferenceValue(REFKEY_VERSION_CHECKER_URL)
            .isEmpty()) {
            UpdateChecker.checkUpdates(this);
        }
    }

    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {

    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     * Register the things that are related to when the server is starting.
     *
     * @param event The Forge event required for this.
     */
    public void onServerStarting(FMLServerStartingEvent event) {
        moduleManager.onServerStarting(event);

        this.baseCommand = constructBaseCommand(event.getServer());
        moduleManager.registerModuleCommand(this.baseCommand, event.getServer());
        BrigadierApi.getCommandDispatcher()
            .register(this.baseCommand);
    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     * Register the things that are related to when the server is starting.
     *
     * @param event The Forge event required for this.
     */
    public void onAboutToStartEvent(FMLServerAboutToStartEvent event) {
        moduleManager.onAboutToStartEvent(event);
        for (WorldStorage worldStorage : worldStorages) {
            worldStorage.onAboutToStartEvent(event);
        }
    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     * Register the things that are related to server starting.
     *
     * @param event The Forge event required for this.
     */
    public void onServerStarted(FMLServerStartedEvent event) {
        moduleManager.onServerStarted(event);
        for (WorldStorage worldStorage : worldStorages) {
            worldStorage.onStartedEvent(event);
        }
    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     * Register the things that are related to server stopping, like persistent storage.
     *
     * @param event The Forge event required for this.
     */
    public void onServerStopping(FMLServerStoppingEvent event) {
        moduleManager.onServerStopping(event);
        for (WorldStorage worldStorage : worldStorages) {
            worldStorage.onStoppingEvent(event);
        }
    }

    /**
     * Override this, call super and annotate with {@link Mod.EventHandler}.
     * Register the things that are related to server stopping, like persistent storage.
     *
     * @param event The Forge event required for this.
     */
    public void onServerStopped(FMLServerStoppedEvent event) {
        moduleManager.onServerStopped(event);
        for (WorldStorage worldStorage : worldStorages) {
            worldStorage.onServerStopped(event);
        }
    }

    /**
     * Register a new world storage type.
     * Make sure to call this at least before the event
     * {@link FMLServerStartedEvent} is called.
     *
     * @param worldStorage The world storage to register.
     */
    public void registerWorldStorage(WorldStorage worldStorage) {
        worldStorages.add(worldStorage);
    }

    /**
     * Construct a creative tab, will only be called once during the init event.
     *
     * @return The default creative tab for items and blocks.
     */
    public abstract CreativeTabs constructDefaultCreativeTab();

    /**
     * Register a config file.
     * The registration order is always kept.
     *
     * @param extendedConfig The config to register.
     */
    public final void registerConfig(ExtendedConfig<?> extendedConfig) {
        getConfigHandler().add(extendedConfig);
    }

    /**
     * Called when the general configs should be registered.
     * These are configs which should be available before other configs can be registered.
     *
     * @param configHandler The config handler to register to.
     */
    public void onGeneralConfigsRegister(ConfigHandler configHandler) {

    }

    /**
     * Called when the main configs should be registered.
     *
     * @param configHandler The config handler to register to.
     */
    public void onMainConfigsRegister(ConfigHandler configHandler) {

    }

    /**
     * @return The default creative tab for items and blocks.
     */
    public final CreativeTabs getDefaultCreativeTab() {
        if (defaultCreativeTab == null) {
            defaultCreativeTab = constructDefaultCreativeTab();
        }
        return defaultCreativeTab;
    }

    /**
     * @return The proxy for this mod, can be null if not required.
     */
    public abstract ICommonProxy getProxy();

    @Override
    public String toString() {
        return getModId();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * Unique references to values that can be registered inside a mod.
     *
     * @param <T> The type of value.
     */
    @Data
    public static class EnumReferenceKey<T> {

        private final String key;
        private final Class<T> type;

        private EnumReferenceKey(String key, Class<T> type) {
            this.key = key;
            this.type = type;
        }

        public static <T> EnumReferenceKey<T> create(String key, Class<T> type) {
            return new EnumReferenceKey<>(key, type);
        }

    }

}
