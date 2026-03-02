package ruiseki.okcore.fluid;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import org.intellij.lang.annotations.MagicConstant;

import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.capability.FluidSink;
import ruiseki.okcore.fluid.capability.FluidSource;
import ruiseki.okcore.fluid.capability.IFluidSink;
import ruiseki.okcore.fluid.capability.IFluidSource;

public class FluidHelpers {

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS;

    public static IFluidSource getFluidSource(Object obj, ForgeDirection side) {
        return getFluidSource(obj, side, DEFAULT);
    }

    public static IFluidSource getFluidSource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = FluidHelpers.class) int usage) {
        if ((usage & FOR_EXTRACTS) == 0) {
            return null;
        }

        if (obj instanceof IFluidSource source) {
            return source;
        }

        if (obj instanceof ICapabilityProvider capabilityProvider) {
            IFluidSource source = capabilityProvider
                .getCapability(CapabilityFluidHandler.FLUID_SOURCE_CAPABILITY, side);

            if (source != null) {
                return source;
            }
        }

        IFluidSource reSource = tryGTNHCapability(obj, IFluidSource.class, side);
        if (reSource != null) {
            return reSource;
        }

        if (obj instanceof IFluidHandler handler) {
            IFluidSource source = new FluidSource(handler, side);
            if (source != null) {
                return source;
            }
        }

        return null;
    }

    public static IFluidSink getFluidSink(Object obj, ForgeDirection side) {
        return getFluidSink(obj, side, DEFAULT);
    }

    public static IFluidSink getFluidSink(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = FluidHelpers.class) int usage) {
        if ((usage & FOR_INSERTS) == 0) {
            return null;
        }

        if (obj instanceof IFluidSink sink) {
            return sink;
        }

        if (obj instanceof ICapabilityProvider capabilityProvider) {
            IFluidSink sink = capabilityProvider.getCapability(CapabilityFluidHandler.FLUID_SINK_CAPABILITY, side);

            if (sink != null) {
                return sink;
            }
        }

        IFluidSink reSink = tryGTNHCapability(obj, IFluidSink.class, side);
        if (reSink != null) {
            return reSink;
        }

        if (obj instanceof IFluidHandler handler) {
            IFluidSink sink = new FluidSink(handler, side);
            if (sink != null) {
                return sink;
            }
        }

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
