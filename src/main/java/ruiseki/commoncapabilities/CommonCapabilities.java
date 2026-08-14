package ruiseki.commoncapabilities;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.commoncapabilities.api.capability.block.BlockCapabilities;
import ruiseki.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesItemStackOredictionary;
import ruiseki.commoncapabilities.api.capability.recipehandler.PrototypedIngredientAlternativesList;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.capability.ingredient.storage.IngredientComponentStorageHandlerConfig;
import ruiseki.commoncapabilities.capability.inventorystate.InventoryStateConfig;
import ruiseki.commoncapabilities.capability.itemhandler.SlotlessItemHandlerConfig;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.commoncapabilities.capability.temperature.TemperatureConfig;
import ruiseki.commoncapabilities.capability.worker.WorkerConfig;
import ruiseki.commoncapabilities.capability.wrench.WrenchConfig;
import ruiseki.commoncapabilities.modcompat.vanilla.VanillaModCompat;
import ruiseki.okcore.config.ConfigHandler;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.modcompat.ModCompatLoader;
import ruiseki.okcore.proxy.ICommonProxy;
import ruiseki.okcore.registries.RegistryEvent;

@Mod(
    modid = Reference.MOD_ID,
    name = Reference.MOD_NAME,
    useMetadata = true,
    version = Reference.MOD_VERSION,
    dependencies = Reference.MOD_DEPENDENCIES,
    guiFactory = "ruiseki.commoncapabilities.GuiConfigOverview$ExtendedConfigGuiFactory")
public class CommonCapabilities extends ModBase {

    @SidedProxy(
        clientSide = "ruiseki.commoncapabilities.proxy.ClientProxy",
        serverSide = "ruiseki.commoncapabilities.proxy.CommonProxy")
    public static ICommonProxy proxy;

    @Mod.Instance(value = Reference.MOD_ID)
    public static CommonCapabilities _instance;

    public CommonCapabilities() {
        super(Reference.MOD_ID, Reference.MOD_NAME);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    protected void loadModCompats(ModCompatLoader modCompatLoader) {
        super.loadModCompats(modCompatLoader);
        modCompatLoader.addModCompat(new VanillaModCompat());
    }

    @Mod.EventHandler
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        IPrototypedIngredientAlternatives.SERIALIZERS.put(
            PrototypedIngredientAlternativesList.SERIALIZER.getId(),
            PrototypedIngredientAlternativesList.SERIALIZER);
        IPrototypedIngredientAlternatives.SERIALIZERS.put(
            PrototypedIngredientAlternativesItemStackOredictionary.SERIALIZER.getId(),
            PrototypedIngredientAlternativesItemStackOredictionary.SERIALIZER);
    }

    @Mod.EventHandler
    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Mod.EventHandler
    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        BlockCapabilities.getInstance()
            .initConstructors();
    }

    @Mod.EventHandler
    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
    }

    @Mod.EventHandler
    @Override
    public void onServerStarted(FMLServerStartedEvent event) {
        super.onServerStarted(event);
    }

    @Mod.EventHandler
    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
    }

    @Override
    public CreativeTabs constructDefaultCreativeTab() {
        return null;
    }

    @Override
    public void onGeneralConfigsRegister(ConfigHandler configHandler) {
        configHandler.add(new GeneralConfig());
    }

    @Override
    public void onMainConfigsRegister(ConfigHandler configHandler) {
        super.onMainConfigsRegister(configHandler);
        configHandler.add(new WorkerConfig());
        configHandler.add(new WrenchConfig());
        configHandler.add(new TemperatureConfig());
        configHandler.add(new InventoryStateConfig());
        configHandler.add(new SlotlessItemHandlerConfig());
        configHandler.add(new RecipeHandlerConfig());
        configHandler.add(new IngredientComponentStorageHandlerConfig());
    }

    @Override
    public ICommonProxy getProxy() {
        return proxy;
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onRegister(RegistryEvent.Register event) {
        if (event.getRegistry() == IngredientComponent.REGISTRY) {
            IPrototypedIngredientAlternatives.SERIALIZERS.put(
                PrototypedIngredientAlternativesList.SERIALIZER.getId(),
                PrototypedIngredientAlternativesList.SERIALIZER);
            IPrototypedIngredientAlternatives.SERIALIZERS.put(
                PrototypedIngredientAlternativesItemStackOredictionary.SERIALIZER.getId(),
                PrototypedIngredientAlternativesItemStackOredictionary.SERIALIZER);

            IngredientComponents.register();
            event.getRegistry()
                .registerAll(
                    IngredientComponents.ITEMSTACK,
                    IngredientComponents.FLUIDSTACK,
                    IngredientComponents.ENERGY);
            IngredientComponent.ITEMSTACK = (IngredientComponent<ItemStack, Integer>) IngredientComponent.REGISTRY
                .getValue(new ResourceLocation("minecraft", "itemstack"));
            IngredientComponent.FLUIDSTACK = (IngredientComponent<FluidStack, Integer>) IngredientComponent.REGISTRY
                .getValue(new ResourceLocation("minecraft", "fluidstack"));
            IngredientComponent.ENERGY = (IngredientComponent<Integer, Boolean>) IngredientComponent.REGISTRY
                .getValue(new ResourceLocation("minecraft", "energy"));
        }
    }

    public static void clog(String message) {
        clog(Level.INFO, message);
    }

    public static void clog(Level level, String message) {
        CommonCapabilities._instance.getLoggerHelper()
            .log(level, message);
    }
}
