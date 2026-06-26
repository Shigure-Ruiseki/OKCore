package ruiseki.okcore.energy.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyWrapper;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
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

    @SubscribeEvent
    public void attachCoFHCapability(AttachCapabilitiesEvent<TileEntity> event) {
        final TileEntity tile = event.getObject();

        if (tile instanceof IEnergyConnection) {
            event.addCapability(ENERGY_CAP, new ICapabilityProvider() {

                private final LazyOptional<CoFHEnergyWrapper>[] energyCache = new LazyOptional[7];

                private int getIndex(@Nullable ForgeDirection facing) {
                    return facing == null ? 6 : facing.ordinal();
                }

                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                    @Nullable ForgeDirection facing) {

                    if (capability == ENERGY || capability == ENERGY_SINK_CAPABILITY
                        || capability == ENERGY_SOURCE_CAPABILITY) {

                        int idx = getIndex(facing);

                        if (energyCache[idx] == null) {
                            energyCache[idx] = LazyOptional.of(() -> new CoFHEnergyWrapper(tile, facing));
                        }

                        return energyCache[idx].cast();
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
