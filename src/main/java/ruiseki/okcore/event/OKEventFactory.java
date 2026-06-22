package ruiseki.okcore.event;

import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;

public class OKEventFactory {

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type,
        T provider) {
        return gatherCapabilities(type, provider, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type,
        T provider, @Nullable ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent<T>((Class<T>) type, provider), parent);
    }

    @Nullable
    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event,
        @Nullable ICapabilityProvider parent) {
        MinecraftForge.EVENT_BUS.post(event);
        return !event.getCapabilities()
            .isEmpty() || parent != null
                ? new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent)
                : null;
    }
}
