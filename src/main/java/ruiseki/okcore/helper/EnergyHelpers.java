package ruiseki.okcore.helper;

import net.minecraftforge.common.util.ForgeDirection;

import org.intellij.lang.annotations.MagicConstant;

import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.energy.capability.IEnergySink;
import ruiseki.okcore.energy.capability.IEnergySource;

public class EnergyHelpers {

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS;

    public static IEnergySource getEnergySource(Object obj, ForgeDirection side) {
        return getEnergySource(obj, side, DEFAULT);
    }

    public static IEnergySource getEnergySource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyHelpers.class) int usage) {
        if ((usage & FOR_EXTRACTS) == 0) return null;

        if (obj instanceof IEnergySource source) {
            return source;
        }

        if (obj instanceof ICapabilityProvider capabilityProvider) {
            IEnergySource source = capabilityProvider.getCapability(CapabilityEnergy.ENERGY_SOURCE_CAPABILITY, side);

            if (source != null) return source;
        }

        return null;
    }

    public static IEnergySink getEnergySink(Object obj, ForgeDirection side) {
        return getEnergySink(obj, side, DEFAULT);
    }

    public static IEnergySink getEnergySink(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyHelpers.class) int usage) {
        if ((usage & FOR_INSERTS) == 0) {
            return null;
        }

        if (obj instanceof IEnergySink sink) {
            return sink;
        }

        if (obj instanceof ICapabilityProvider capabilityProvider) {
            IEnergySink sink = capabilityProvider.getCapability(CapabilityEnergy.ENERGY_SINK_CAPABILITY, side);

            if (sink != null) {
                return sink;
            }
        }

        return null;
    }
}
