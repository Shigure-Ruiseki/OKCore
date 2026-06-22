package ruiseki.okcore.fluid.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.fluid.IFluidHandlerItem;
import ruiseki.okcore.init.IInitListener;

public class CapabilityFluidHandler implements IInitListener {

    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;

    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

    @CapabilityInject(IFluidSink.class)
    public static Capability<IFluidSink> FLUID_SINK_CAPABILITY = null;

    @CapabilityInject(IFluidSource.class)
    public static Capability<IFluidSource> FLUID_SOURCE_CAPABILITY = null;

    @Override
    public void onInit(Step initStep) {
        if (initStep != Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IFluidHandler.class);
        CapabilityManager.INSTANCE.register(IFluidHandlerItem.class);
        CapabilityManager.INSTANCE.register(IFluidSink.class);
        CapabilityManager.INSTANCE.register(IFluidSource.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack == null || stack.getItem() == null) return;
        if (stack.getItem() instanceof IItemCapability) return;
        if (stack.getItem() instanceof IFluidContainerItem legacy) {
            event.addCapability(
                new ResourceLocation(Reference.MOD_ID, "fluid_container"),
                new FluidContainerWrapper(stack, legacy));
        }
    }
}
