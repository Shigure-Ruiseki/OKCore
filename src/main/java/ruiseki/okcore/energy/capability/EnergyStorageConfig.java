package ruiseki.okcore.energy.capability;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.OKCore;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

public class EnergyStorageConfig extends CapabilityConfig<IEnergyStorage> {

    /**
     * The unique instance.
     */
    public static EnergyStorageConfig _instance;

    @CapabilityInject(IEnergyStorage.class)
    public static Capability<IEnergyStorage> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public EnergyStorageConfig() {
        super(
            OKCore._instance,
            true,
            "energy_storage",
            "A container or block entity that can handle and store energy.",
            IEnergyStorage.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
