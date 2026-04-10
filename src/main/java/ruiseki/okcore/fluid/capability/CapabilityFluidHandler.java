package ruiseki.okcore.fluid.capability;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.capabilities.IItemCapability;
import ruiseki.okcore.event.AttachCapabilitiesEvent;
import ruiseki.okcore.fluid.FluidHandlerItem;
import ruiseki.okcore.fluid.IFluidHandlerItem;
import ruiseki.okcore.fluid.SmartTank;
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
        if (initStep == Step.PREINIT) {
            register();
        }
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IFluidHandler.class, new Capability.IStorage<IFluidHandler>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IFluidHandler> capability, IFluidHandler instance,
                EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IFluidHandler> capability, IFluidHandler instance, EnumFacing side,
                NBTBase nbt) {

            }
        }, () -> new SmartTank(FluidContainerRegistry.BUCKET_VOLUME));
        CapabilityManager.INSTANCE.register(IFluidHandlerItem.class, new Capability.IStorage<IFluidHandlerItem>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IFluidHandlerItem> capability, IFluidHandlerItem instance,
                EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IFluidHandlerItem> capability, IFluidHandlerItem instance, EnumFacing side,
                NBTBase nbt) {

            }
        }, () -> new FluidHandlerItem(new ItemStack(Items.bucket), FluidContainerRegistry.BUCKET_VOLUME));
        CapabilityManager.INSTANCE.register(IFluidSink.class, new Capability.IStorage<IFluidSink>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IFluidSink> capability, IFluidSink instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IFluidSink> capability, IFluidSink instance, EnumFacing side, NBTBase nbt) {

            }
        }, () -> new FluidSink(null, null));
        CapabilityManager.INSTANCE.register(IFluidSource.class, new Capability.IStorage<IFluidSource>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IFluidSource> capability, IFluidSource instance,
                EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IFluidSource> capability, IFluidSource instance, EnumFacing side,
                NBTBase nbt) {

            }
        }, () -> new FluidSource(null, null));

        MinecraftForge.EVENT_BUS.register(new CapabilityFluidHandler());
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        if (event.getType() != ItemStack.class) return;
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
