package ruiseki.okcore.capabilities.fluid;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.fluid.FluidHandlerItem;
import ruiseki.okcore.fluid.IFluidHandlerItem;
import ruiseki.okcore.fluid.SmartTank;
import ruiseki.okcore.init.IInitListener;

public class CapabilityFluidHandler implements IInitListener {

    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;
    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            register();
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE
            .register(IFluidHandler.class, () -> new SmartTank(FluidContainerRegistry.BUCKET_VOLUME));
        CapabilityManager.INSTANCE.register(
            IFluidHandlerItem.class,
            () -> new FluidHandlerItem(new ItemStack(Items.bucket), FluidContainerRegistry.BUCKET_VOLUME));

        MinecraftForge.EVENT_BUS.register(new CapabilityFluidHandler());
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack == null) return;
        if (stack.getItem() instanceof IItemCapability) return;
        if (stack.getItem() instanceof IFluidContainerItem legacy) {
            event.addCapability(
                new ResourceLocation(Reference.MOD_ID, "fluid_container"),
                new FluidContainerAdapter(stack, legacy));
        }
    }
}
