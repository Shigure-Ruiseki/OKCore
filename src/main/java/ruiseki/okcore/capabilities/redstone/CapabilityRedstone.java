package ruiseki.okcore.capabilities.redstone;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.block.IDynamicRedstone;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.init.IInitListener;

public class CapabilityRedstone implements IInitListener {

    @CapabilityInject(IDynamicRedstone.class)
    public static Capability<IDynamicRedstone> DYNAMIC_REDSTONE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDynamicRedstone.class, new Capability.IStorage<IDynamicRedstone>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IDynamicRedstone> capability, IDynamicRedstone instance,
                ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IDynamicRedstone> capability, IDynamicRedstone instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, DynamicRedstoneDefault::new);
    }

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (initStep == IInitListener.Step.PREINIT) {
            register();
        }
    }
}
