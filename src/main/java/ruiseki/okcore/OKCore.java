package ruiseki.okcore;

import java.io.File;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;

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
import ruiseki.okcore.capabilities.light.CapabilityLight;
import ruiseki.okcore.capabilities.redstone.CapabilityRedstone;
import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.command.CommandOKCore;
import ruiseki.okcore.core.ModItems;
import ruiseki.okcore.data.DataHandler;
import ruiseki.okcore.data.DataLoader;
import ruiseki.okcore.data.loader.baubles.BaubleSlotHandler;
import ruiseki.okcore.data.loader.conditional.LoadConditionHandler;
import ruiseki.okcore.datacomponent.init.DataComponents;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.event.data.OKDataEvent;
import ruiseki.okcore.event.inventory.InventoryScanner;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.guide.GuideGuiHandler;
import ruiseki.okcore.guide.GuideHandler;
import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.lib.LibMods;
import ruiseki.okcore.proxy.ICommonProxy;
import ruiseki.okcore.recipe.NBTShapedOreRecipe;
import ruiseki.okcore.recipe.NBTShapelessOreRecipe;
import ruiseki.okcore.recipe.RecipeRegistries;

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

        addInitListeners(new CapabilityItemHandler());
        addInitListeners(new CapabilityFluidHandler());
        addInitListeners(new CapabilityEnergy());
        addInitListeners(new CapabilityLight());
        addInitListeners(new CapabilityRedstone());
        addInitListeners(new CapabilityGuide());

        addInitListeners(new DataComponents());
        addInitListeners(new InventoryScanner());
        addInitListeners(new GuideHandler());
        addInitListeners(new BaubleSlotHandler());
    }

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        ASMDataTable asmData = event.getASMHarvestedData();

        CapabilityManager.INSTANCE.injectCapabilities(asmData);
        GuideHandler.loadFromASM(asmData);
        LoadConditionHandler.loadFromASM(asmData);
        RecipeRegistries.loadFromASM(asmData);
        DataHandler.loadFromASM(asmData);
    }

    @Override
    protected CommandMod constructBaseCommand() {
        CommandMod command = new CommandOKCore(this, Maps.newHashMap());
        command.addAlias("ok");
        return command;
    }

    @Override
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.post(new OKDataEvent.Pre());
        DataLoader.loadAllData();

        ModItems.preInit();
        if (LibMods.Waila.isLoaded()) {
            BlockProvider.init();
        }

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuideGuiHandler());
    }

    @Override
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event);

        RecipeSorter.register(
            Reference.PREFIX_MOD + "nbtshaped",
            NBTShapedOreRecipe.class,
            RecipeSorter.Category.SHAPED,
            "after:minecraft:shaped");
        RecipeSorter.register(
            Reference.PREFIX_MOD + "nbtshapeless",
            NBTShapelessOreRecipe.class,
            RecipeSorter.Category.SHAPELESS,
            "after:minecraft:shapeless");
    }

    @Override
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        MinecraftForge.EVENT_BUS.post(new OKDataEvent.Post());
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {

        MinecraftServer server = event.getServer();
        File worldDir = new File(server.getFolderName());
        MinecraftForge.EVENT_BUS.post(new OKDataEvent.WorldPre(server, worldDir));
        DataLoader.loadWorldData(worldDir);
        MinecraftForge.EVENT_BUS.post(new OKDataEvent.WorldPost(server, worldDir));
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
        MinecraftForge.EVENT_BUS.post(new OKDataEvent.WorldUnload());
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
