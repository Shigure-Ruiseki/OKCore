package ruiseki.okcore.energy.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyProvider;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyReceiver;
import ruiseki.okcore.energy.capability.cofh.CoFHHandlerWrapper;
import ruiseki.okcore.energy.capability.cofh.CoFHProviderWrapper;
import ruiseki.okcore.energy.capability.cofh.CoFHReceiverWrapper;
import ruiseki.okcore.energy.capability.ok.OKEnergySink;
import ruiseki.okcore.energy.capability.ok.OKEnergySource;
import ruiseki.okcore.event.AttachCapabilitiesEvent;
import ruiseki.okcore.init.IInitListener;

@SuppressWarnings("unchecked")
public class CapabilityEnergy implements IInitListener {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY = null;

    @CapabilityInject(IEnergySink.class)
    public static Capability<IEnergySink> ENERGY_SINK_CAPABILITY = null;

    @CapabilityInject(IEnergySource.class)
    public static Capability<IEnergySource> ENERGY_SOURCE_CAPABILITY = null;

    public static final ResourceLocation ENERGY_CAP = new ResourceLocation(Reference.MOD_ID, "energy");

    public static void register() {
        CapabilityManager.INSTANCE.register(IEnergyStorage.class, new Capability.IStorage<IEnergyStorage>() {

            @Override
            public NBTBase writeNBT(Capability<IEnergyStorage> capability, IEnergyStorage instance,
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
            public NBTBase writeNBT(Capability<IEnergySink> capability, IEnergySink instance, ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IEnergySink> capability, IEnergySink instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, () -> new OKEnergySink(null, null));
        CapabilityManager.INSTANCE.register(IEnergySource.class, new Capability.IStorage<IEnergySource>() {

            @Override
            public NBTBase writeNBT(Capability<IEnergySource> capability, IEnergySource instance, ForgeDirection side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IEnergySource> capability, IEnergySource instance, ForgeDirection side,
                NBTBase nbt) {

            }
        }, () -> new OKEnergySource(null, null));
    }

    @SubscribeEvent
    public void attachCoFHCapability(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getType() != TileEntity.class) return;
        TileEntity tile = event.getObject();

        if (tile instanceof IEnergyConnection) {
            event.addCapability(ENERGY_CAP, new ICapabilityProvider() {

                @Override
                public boolean hasCapability(@NotNull Capability<?> capability, ForgeDirection facing) {
                    return capability == ENERGY || capability == ENERGY_SINK_CAPABILITY
                        || capability == ENERGY_SOURCE_CAPABILITY;
                }

                @Override
                public <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
                    if (capability == ENERGY) {
                        if (tile instanceof IEnergyHandler handler) return (T) new CoFHHandlerWrapper(handler, facing);
                        if (tile instanceof IEnergyReceiver receiver)
                            return (T) new CoFHReceiverWrapper(receiver, facing);
                        if (tile instanceof IEnergyProvider provider)
                            return (T) new CoFHProviderWrapper(provider, facing);
                    }

                    if (capability == ENERGY_SINK_CAPABILITY && tile instanceof IEnergyReceiver receiver) {
                        return (T) new CoFHEnergyReceiver(receiver, facing);
                    }

                    if (capability == ENERGY_SOURCE_CAPABILITY && tile instanceof IEnergyProvider provider) {
                        return (T) new CoFHEnergyProvider(provider, facing);
                    }

                    return null;
                }
            });
        }
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            register();
            MinecraftForge.EVENT_BUS.register(this);
        }
    }
}
