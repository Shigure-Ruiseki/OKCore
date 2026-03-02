package ruiseki.okcore.capabilities.energy;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.capabilities.CapabilityManager;
import ruiseki.okcore.init.IInitListener;

public class CapabilityEnergy implements IInitListener {

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> ENERGY_CAPABILITY = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(IEnergyStorage.class, EnergyStorageDefault::new);
    }

    @Override
    public void onInit(Step initStep) {
        if (initStep == Step.PREINIT) {
            register();
        }
    }
}
