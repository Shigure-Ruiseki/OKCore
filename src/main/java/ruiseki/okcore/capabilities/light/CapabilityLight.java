package ruiseki.okcore.capabilities.light;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.block.IDynamicLight;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;

public class CapabilityLight {

    @CapabilityInject(IDynamicLight.class)
    public static Capability<IDynamicLight> DYNAMIC_LIGHT_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IDynamicLight.class, new Capability.IStorage<IDynamicLight>() {

            @Override
            public NBTBase writeNBT(Capability<IDynamicLight> capability, IDynamicLight instance, ForgeDirection side) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("LightLevel", instance.getLightLevel());
                return tag;
            }

            @Override
            public void readNBT(Capability<IDynamicLight> capability, IDynamicLight instance, ForgeDirection side,
                NBTBase nbt) {
                if (nbt instanceof NBTTagCompound tag) {
                    instance.setLightLevel(tag.getInteger("LightLevel"));
                }
            }

        }, DynamicLightDefault::new);
    }
}
