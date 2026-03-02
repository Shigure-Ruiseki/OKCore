package ruiseki.okcore.energy;

import net.minecraftforge.common.util.ForgeDirection;

import org.intellij.lang.annotations.MagicConstant;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.energy.capability.IEnergyIO;
import ruiseki.okcore.energy.capability.IEnergySink;
import ruiseki.okcore.energy.capability.IEnergySource;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyHandler;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyProvider;
import ruiseki.okcore.energy.capability.cofh.CoFHEnergyReceiver;
import ruiseki.okcore.energy.capability.ok.OKEnergySink;
import ruiseki.okcore.energy.capability.ok.OKEnergySource;

public class EnergyHelpers {

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int WRAP_COFH = 0b1 << counter++;
    public static final int WRAP_IC2 = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS | WRAP_COFH | WRAP_IC2;

    public static IEnergySource getEnergySource(Object obj, ForgeDirection side) {
        return getEnergySource(obj, side, DEFAULT);
    }

    public static IEnergySource getEnergySource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyHelpers.class) int usage) {
        if ((usage & FOR_EXTRACTS) == 0) {
            return null;
        }

        if (obj instanceof IEnergySource source) {
            return source;
        }

        if (obj instanceof ICapabilityProvider capabilityProvider) {
            IEnergySource source = capabilityProvider.getCapability(CapabilityEnergy.ENERGY_SOURCE_CAPABILITY, side);

            if (source != null) {
                return source;
            }
        }

        IEnergySource reSource = tryGTNHCapability(obj, IEnergySource.class, side);
        if (reSource != null) {
            return reSource;
        }

        if (obj instanceof IOKEnergySource provider) {
            IEnergySource source = new OKEnergySource(provider, side);

            if (source != null) {
                return source;
            }
        }

        if ((usage & WRAP_COFH) != 0) {
            IEnergyIO cofh = wrapCoFHEnergy(obj, side);
            if (cofh != null) {
                return cofh;
            }
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

        IEnergySink reSink = tryGTNHCapability(obj, IEnergySink.class, side);
        if (reSink != null) {
            return reSink;
        }

        if (obj instanceof IOKEnergySink receiver) {
            IEnergySink source = new OKEnergySink(receiver, side);

            if (source != null) {
                return source;
            }
        }

        if ((usage & WRAP_COFH) != 0) {
            IEnergyIO cofh = wrapCoFHEnergy(obj, side);
            if (cofh != null) {
                return cofh;
            }
        }

        return null;
    }

    public static IEnergyIO wrapCoFHEnergy(Object obj, ForgeDirection side) {

        try {
            // IEnergyHandler
            Class<?> handlerClass = Class.forName("cofh.api.energy.IEnergyHandler");
            if (handlerClass.isInstance(obj)) {
                return new CoFHEnergyHandler((IEnergyHandler) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        try {
            // IEnergyProvider
            Class<?> providerClass = Class.forName("cofh.api.energy.IEnergyProvider");
            if (providerClass.isInstance(obj)) {
                return new CoFHEnergyProvider((IEnergyProvider) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        try {
            // IEnergyReceiver
            Class<?> receiverClass = Class.forName("cofh.api.energy.IEnergyReceiver");
            if (receiverClass.isInstance(obj)) {
                return new CoFHEnergyReceiver((IEnergyReceiver) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T tryGTNHCapability(Object obj, Class<T> clazz, ForgeDirection side) {

        try {
            Class<?> providerClass = Class.forName("com.gtnewhorizon.gtnhlib.capability.CapabilityProvider");

            if (!providerClass.isInstance(obj)) {
                return null;
            }

            Object result = providerClass.getMethod("getCapability", Class.class, ForgeDirection.class)
                .invoke(obj, clazz, side);

            return (T) result;

        } catch (Throwable ignored) {
            return null;
        }
    }
}
