package ruiseki.okcore;

import net.minecraft.command.ICommandSender;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.Level;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import ruiseki.okcore.addon.waila.BlockProvider;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.command.CommandDatapack;
import ruiseki.okcore.config.ModConfig;
import ruiseki.okcore.core.OKCoreBlocks;
import ruiseki.okcore.core.OKCoreItems;
import ruiseki.okcore.data.DatapackLoader;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.enums.Mods;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.guide.GuideGuiHandler;
import ruiseki.okcore.guide.GuideRegistry;
import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.proxy.ICommonProxy;

@Mod(
    modid = Reference.MOD_ID,
    name = Reference.MOD_NAME,
    version = Reference.VERSION,
    dependencies = Reference.DEPENDENCIES,
    guiFactory = Reference.GUI_FACTORY)
public class OKCore extends ModBase {

    @SidedProxy(serverSide = Reference.PROXY_COMMON, clientSide = Reference.PROXY_CLIENT)
    public static ICommonProxy proxy;

    @Mod.Instance(Reference.MOD_ID)
    public static OKCore instance;

    public OKCore() {
        super(Reference.MOD_ID, Reference.MOD_NAME);
        putGenericReference(REFKEY_MOD_VERSION, Reference.VERSION);
        putGenericReference(REFKEY_VERSION_CHECKER, ModConfig.useVersionChecker);
        putGenericReference(REFKEY_VERSION_CHECKER_URL, Reference.UPDATE_URL);

        addInitListeners(new CapabilityItemHandler());
        addInitListeners(new CapabilityFluidHandler());
        addInitListeners(new CapabilityEnergy());
        addInitListeners(new CapabilityGuide());

        addInitListeners(new GuideRegistry());
    }

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        ASMDataTable asmData = event.getASMHarvestedData();

        CapabilityManager.INSTANCE.injectCapabilities(asmData);
        GuideRegistry.loadFromASM(asmData);
    }

    @Override
    protected LiteralArgumentBuilder<ICommandSender> constructBaseCommand(MinecraftServer server) {
        LiteralArgumentBuilder<ICommandSender> root = super.constructBaseCommand(server);
        root.then(new CommandDatapack(this, server).make());
        return root;
    }

    @Override
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        OKCoreItems.register();
        OKCoreBlocks.register();
        if (Mods.Waila.isModLoaded()) {
            BlockProvider.init();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuideGuiHandler());
    }

    @Override
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        super.onServerAboutToStart(event);
        DatapackLoader.loadAllDataAtServerStart(event.getServer());
    }

    @Override
    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Override
    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        super.onServerStopped(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return null;
    }

    @Override
    public ICommonProxy getProxy() {
        return proxy;
    }

    /**
     * Log a new info message for this mod.
     *
     * @param message The message to show.
     */
    public static void okLog(String message) {
        OKCore.instance.log(Level.INFO, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     */
    public static void okLog(Level level, String message) {
        OKCore.instance.log(level, message);
    }

    /**
     * Log a new message of the given level for this mod.
     *
     * @param level   The level in which the message must be shown.
     * @param message The message to show.
     * @param params  Parameters to replace in the message.
     */
    public static void okLog(Level level, String message, Object... params) {
        OKCore.instance.log(level, message, params);
    }
}
