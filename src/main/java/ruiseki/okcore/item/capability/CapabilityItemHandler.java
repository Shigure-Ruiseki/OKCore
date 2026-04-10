package ruiseki.okcore.item.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.item.IItemHandler;
import ruiseki.okcore.item.ItemStackHandler;

public class CapabilityItemHandler implements IInitListener {

    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IItemHandler.class, new Capability.IStorage<IItemHandler>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IItemHandler> capability, IItemHandler instance,
                EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IItemHandler> capability, IItemHandler instance, EnumFacing side,
                NBTBase nbt) {

            }
        }, ItemStackHandler::new);
    }

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (initStep == IInitListener.Step.PREINIT) {
            register();
        }
    }
}
