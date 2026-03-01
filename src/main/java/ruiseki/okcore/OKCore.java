package ruiseki.okcore;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.oredict.RecipeSorter;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.capabilities.fluid.CapabilityFluidHandler;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.ICommonProxy;
import ruiseki.okcore.recipe.NBTShapedOreRecipe;
import ruiseki.okcore.recipe.NBTShapelessOreRecipe;

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
        addInitListeners(new CapabilityFluidHandler());
    }

    @Mod.EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        CapabilityManager.INSTANCE.injectCapabilities(event.getASMHarvestedData());
    }

    @Override
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
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
}
