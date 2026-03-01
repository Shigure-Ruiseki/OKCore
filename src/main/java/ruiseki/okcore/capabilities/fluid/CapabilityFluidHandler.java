package ruiseki.okcore.capabilities.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.fluid.FluidHandlerItemStack;
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
        CapabilityManager.INSTANCE.register(
            IFluidHandler.class,
            new DefaultFluidHandlerStorage<>(),
            () -> new SmartTank(FluidContainerRegistry.BUCKET_VOLUME));

        CapabilityManager.INSTANCE.register(
            IFluidHandlerItem.class,
            new DefaultFluidHandlerStorage<>(),
            () -> new FluidHandlerItemStack(null, 0));

        MinecraftForge.EVENT_BUS.register(new CapabilityFluidHandler());
    }

    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {

        System.out.println("[OKCORE] AttachCapabilitiesEvent fired for: " + event.getObject());

        if (event.getObject() == null) {
            System.out.println("[OKCORE] ItemStack is null!");
            return;
        }

        if (event.getType() == ItemStack.class) {

            ItemStack stack = event.getObject();
            System.out.println("[OKCORE] Processing ItemStack: " + stack);

            if (stack.getItem() instanceof IFluidContainerItem) {

                IFluidContainerItem containerItem = (IFluidContainerItem) stack.getItem();
                int capacity = containerItem.getCapacity(stack);

                System.out.println("[OKCORE] Item is IFluidContainerItem. Capacity = " + capacity);

                event.addCapability(
                    new ResourceLocation(Reference.MOD_ID, "fluid_handler"),
                    new FluidHandlerItemStack(stack, capacity)
                );

                System.out.println("[OKCORE] Fluid capability attached.");
            } else {
                System.out.println("[OKCORE] Item is NOT IFluidContainerItem.");
            }
        } else {
            System.out.println("[OKCORE] Event type mismatch: " + event.getType());
        }
    }

    private static class DefaultFluidHandlerStorage<T extends IFluidHandler> implements Capability.IStorage<T> {

        @Override
        public NBTBase writeNBT(Capability<T> capability, T instance, ForgeDirection side) {
            if (!(instance instanceof IFluidTank tank))
                throw new RuntimeException("IFluidHandler instance does not implement IFluidTank");
            NBTTagCompound nbt = new NBTTagCompound();
            FluidStack fluid = tank.getFluid();
            if (fluid != null) {
                fluid.writeToNBT(nbt);
            } else {
                nbt.setString("Empty", "");
            }
            nbt.setInteger("Capacity", tank.getCapacity());
            return nbt;
        }

        @Override
        public void readNBT(Capability<T> capability, T instance, ForgeDirection side, NBTBase nbt) {
            if (!(instance instanceof FluidTank tank))
                throw new RuntimeException("IFluidHandler instance is not instance of FluidTank");
            NBTTagCompound tags = (NBTTagCompound) nbt;
            tank.setCapacity(tags.getInteger("Capacity"));
            tank.readFromNBT(tags);
        }
    }
}
