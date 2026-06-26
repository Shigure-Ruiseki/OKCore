package ruiseki.okcore.energy.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyHandler;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyProvider;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyReceiver;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.helper.EnderIOHelpers;
import ruiseki.okcore.init.IInitListener;
import ruiseki.okcore.lib.LibMods;

@SuppressWarnings("unchecked")
public class CapabilityEnergy implements IInitListener {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY = null;

    @CapabilityInject(IEnergySink.class)
    public static Capability<IEnergySink> ENERGY_SINK_CAPABILITY = null;

    @CapabilityInject(IEnergySource.class)
    public static Capability<IEnergySource> ENERGY_SOURCE_CAPABILITY = null;

    public static final ResourceLocation ENERGY_CAP = new ResourceLocation(Reference.MOD_ID, "energy");

    private static final boolean isEnderIOLoaded = LibMods.EnderIO.isModLoaded();

    @SubscribeEvent
    public void attachCoFHCapability(AttachCapabilitiesEvent<TileEntity> event) {
        final TileEntity tile = event.getObject();

        if (tile instanceof IEnergyConnection) {
            event.addCapability(ENERGY_CAP, new ICapabilityProvider() {

                private final LazyOptional<IEnergyStorage>[] energyCache = new LazyOptional[7];
                private final LazyOptional<IEnergySink>[] sinkCache = new LazyOptional[7];
                private final LazyOptional<IEnergySource>[] sourceCache = new LazyOptional[7];

                private int getIndex(@Nullable ForgeDirection facing) {
                    return facing == null ? 6 : facing.ordinal();
                }

                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                    @Nullable ForgeDirection facing) {
                    int idx = getIndex(facing);

                    if (capability == ENERGY) {
                        if (energyCache[idx] == null) {
                            if (isEnderIOLoaded) {
                                energyCache[idx] = EnderIOHelpers.getEnergyCap(tile, facing);
                            }

                            if (energyCache[idx] == null) {
                                if (tile instanceof IEnergyHandler handler) {
                                    energyCache[idx] = LazyOptional.of(() -> new CoFHEnergyHandler(handler, facing));
                                } else if (tile instanceof IEnergyReceiver receiver) {
                                    energyCache[idx] = LazyOptional.of(() -> new CoFHEnergyReceiver(receiver, facing));
                                } else if (tile instanceof IEnergyProvider provider) {
                                    energyCache[idx] = LazyOptional.of(() -> new CoFHEnergyProvider(provider, facing));
                                }
                            }
                        }
                        if (energyCache[idx] != null) {
                            return energyCache[idx].cast();
                        }
                    }

                    if (capability == ENERGY_SINK_CAPABILITY) {
                        if (sinkCache[idx] == null) {
                            if (isEnderIOLoaded) {
                                sinkCache[idx] = EnderIOHelpers.getSinkCap(tile, facing);
                            }
                            if (sinkCache[idx] == null) {
                                if (tile instanceof IEnergyReceiver receiver) {
                                    sinkCache[idx] = LazyOptional.of(() -> new CoFHEnergyReceiver(receiver, facing));
                                }
                            }
                        }
                        if (sinkCache[idx] != null) {
                            return sinkCache[idx].cast();
                        }
                    }

                    if (capability == ENERGY_SOURCE_CAPABILITY) {
                        if (sourceCache[idx] == null) {
                            if (isEnderIOLoaded) {
                                sourceCache[idx] = EnderIOHelpers.getSourceCap(tile, facing);
                            }
                            if (sourceCache[idx] == null) {
                                if (tile instanceof IEnergyProvider provider) {
                                    sourceCache[idx] = LazyOptional.of(() -> new CoFHEnergyProvider(provider, facing));
                                }
                            }
                        }
                        if (sourceCache[idx] != null) {
                            return sourceCache[idx].cast();
                        }
                    }

                    return LazyOptional.empty();
                }
            });
        }
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep != Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IEnergyStorage.class);
        CapabilityManager.INSTANCE.register(IEnergySink.class);
        CapabilityManager.INSTANCE.register(IEnergySource.class);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
