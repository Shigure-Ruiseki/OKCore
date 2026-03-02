package ruiseki.okcore.energy.capability;

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
        CapabilityManager.INSTANCE.register(IEnergyStorage.class, EnergyStorageDefault::new);
        CapabilityManager.INSTANCE.register(IEnergySink.class, () -> new OKEnergySink(null, null));
        CapabilityManager.INSTANCE.register(IEnergySource.class, () -> new OKEnergySource(null, null));
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            register();
        }
    }
}
