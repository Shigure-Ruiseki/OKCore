package ruiseki.okcore.capabilities.light;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.block.IDynamicLight;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.init.IInitListener;

public class CapabilityLight implements IInitListener {

    @CapabilityInject(IDynamicLight.class)
    public static Capability<IDynamicLight> DYNAMIC_LIGHT_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDynamicLight.class, new Capability.IStorage<IDynamicLight>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IDynamicLight> capability, IDynamicLight instance,
                EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IDynamicLight> capability, IDynamicLight instance, EnumFacing side,
                NBTBase nbt) {

            }
        }, DynamicLightDefault::new);
    }

    @Override
    public void onInit(IInitListener.Step initStep) {
        if (initStep == IInitListener.Step.PREINIT) {
            register();
        }
    }
}
