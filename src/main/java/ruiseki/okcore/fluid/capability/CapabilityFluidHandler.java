package ruiseki.okcore.fluid.capability;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidContainerItem;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.fluid.IFluidHandler;
import ruiseki.okcore.fluid.IFluidHandlerItem;
import ruiseki.okcore.fluid.capability.wrapper.FluidBucketWrapper;
import ruiseki.okcore.fluid.capability.wrapper.FluidContainerWrapper;
import ruiseki.okcore.fluid.capability.wrapper.FluidHandlerWrapperProvider;
import ruiseki.okcore.init.IInitListener;

public class CapabilityFluidHandler implements IInitListener {

    @CapabilityInject(IFluidHandler.class)
    public static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;

    @CapabilityInject(IFluidHandlerItem.class)
    public static Capability<IFluidHandlerItem> FLUID_HANDLER_ITEM_CAPABILITY = null;

    @Override
    public void onInit(Step initStep) {
        if (initStep != Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IFluidHandler.class);
        CapabilityManager.INSTANCE.register(IFluidHandlerItem.class);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack == null || stack.getItem() == null) return;
        Item item = stack.getItem();
        if (item instanceof IFluidContainerItem legacy) {
            event.addCapability(
                new ResourceLocation(Reference.MOD_ID, "fluid_container_wrapper"),
                new FluidContainerWrapper(stack, legacy));
        }
        if (item == Items.bucket || item == Items.water_bucket
            || item == Items.lava_bucket
            || item == Items.milk_bucket
            || item instanceof ItemBucket
            || item instanceof ItemBucketMilk) {
            event.addCapability(
                new ResourceLocation(Reference.MOD_ID, "fluid_bucker_wrapper"),
                new FluidBucketWrapper(stack));
        }
    }

    @SubscribeEvent
    public void attachTileCapability(AttachCapabilitiesEvent<TileEntity> event) {
        TileEntity tile = event.getObject();
        if (tile instanceof IFluidHandler) return;
        if (!(tile instanceof net.minecraftforge.fluids.IFluidHandler handler)) return;
        event.addCapability(
            new ResourceLocation(Reference.MOD_ID, "fluid_handler_warpper"),
            new FluidHandlerWrapperProvider(handler));
    }

}
