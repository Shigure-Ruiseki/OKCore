package ruiseki.okcore.energy.capability;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.Reference;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.energy.capability.wrapper.EnergyStorageWrapper;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;
import ruiseki.okcore.init.IInitListener;

public class CapabilityEnergy implements IInitListener {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY = null;

    public static final ResourceLocation ENERGY_CAP = new ResourceLocation(Reference.MOD_ID, "energy");

    @SubscribeEvent
    public void attachCoFHCapability(AttachCapabilitiesEvent<TileEntity> event) {
        final TileEntity tile = event.getObject();
        if (!(tile instanceof IEnergyConnection)) return;
        event.addCapability(ENERGY_CAP, new EnergyStorageWrapper(tile));
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep != Step.PREINIT) return;
        CapabilityManager.INSTANCE.register(IEnergyStorage.class);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
