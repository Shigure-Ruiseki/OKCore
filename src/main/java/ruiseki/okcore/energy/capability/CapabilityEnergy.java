package ruiseki.okcore.energy.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.energy.capability.ok.OKEnergySink;
import ruiseki.okcore.energy.capability.ok.OKEnergySource;
import ruiseki.okcore.init.IInitListener;

public class CapabilityEnergy implements IInitListener {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY_CAPABILITY = null;

    @CapabilityInject(IEnergySink.class)
    public static Capability<IEnergySink> ENERGY_SINK_CAPABILITY = null;

    @CapabilityInject(IEnergySource.class)
    public static Capability<IEnergySource> ENERGY_SOURCE_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IEnergyStorage.class, new Capability.IStorage<IEnergyStorage>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance,
                ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, EnergyStorageDefault::new);
        CapabilityManager.INSTANCE.register(IEnergySink.class, new Capability.IStorage<IEnergySink>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IEnergySink> capability, IEnergySink instance,
                ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IEnergySink> capability, IEnergySink instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, () -> new OKEnergySink(null, null));
        CapabilityManager.INSTANCE.register(IEnergySource.class, new Capability.IStorage<IEnergySource>() {

            @Override
            public @Nullable NBTBase writeNBT(Capability<IEnergySource> capability, IEnergySource instance,
                ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IEnergySource> capability, IEnergySource instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, () -> new OKEnergySource(null, null));
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            register();
        }
    }
}
