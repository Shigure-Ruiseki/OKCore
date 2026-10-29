
package ruiseki.okcore.event;

import net.minecraftforge.common.MinecraftForge;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.CapabilityDispatcher;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;

/**
 * Factory methods for gathering and attaching capabilities to providers.
 *
 * <p>
 * This class dispatches {@link AttachCapabilitiesEvent} through the
 * {@link MinecraftForge#EVENT_BUS}, allowing registered event listeners
 * to contribute capabilities and associated listeners to a provider.
 * </p>
 *
 * <p>
 * Collected capabilities are combined into a {@link CapabilityDispatcher}.
 * If no capabilities are registered and no parent provider is supplied,
 * {@code null} is returned.
 * </p>
 *
 * @see AttachCapabilitiesEvent
 * @see CapabilityDispatcher
 * @see ICapabilityProvider
 */
public class OKEventFactory {

    /**
     * Gathers capabilities for the specified provider without a parent.
     *
     * <p>
     * This is equivalent to calling
     * {@link #gatherCapabilities(Class, ICapabilityProvider, ICapabilityProvider)}
     * with a {@code null} parent.
     * </p>
     *
     * @param <T>      the type of capability provider
     * @param type     the provider class used for capability attachment
     * @param provider the provider instance receiving capabilities
     * @return the collected capability dispatcher, or {@code null} if
     *         no capabilities were attached
     */
    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type,
        T provider) {
        return gatherCapabilities(type, provider, null);
    }

    /**
     * Gathers capabilities for the specified provider and optional parent.
     *
     * <p>
     * Creates an {@link AttachCapabilitiesEvent} for the given provider
     * and posts it to the Forge event bus. Registered listeners may
     * attach capabilities before the resulting dispatcher is created.
     * </p>
     *
     * <p>
     * A {@link CapabilityDispatcher} is created when at least one
     * capability is attached or a parent provider is present.
     * Otherwise, {@code null} is returned.
     * </p>
     *
     * @param <T>      the type of capability provider
     * @param type     the provider class used for capability attachment
     * @param provider the provider instance receiving capabilities
     * @param parent   the optional parent capability provider
     * @return the collected capability dispatcher, or {@code null} if
     *         no capabilities were attached and no parent was provided
     *
     * @see AttachCapabilitiesEvent
     */
    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends ICapabilityProvider> CapabilityDispatcher gatherCapabilities(Class<? extends T> type,
        T provider, @Nullable ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent<T>((Class<T>) type, provider), parent);
    }

    /**
     * Dispatches the capability attachment event and creates a dispatcher
     * from the collected capabilities, listeners, and optional parent.
     *
     * @param event  the capability attachment event to dispatch
     * @param parent the optional parent capability provider
     * @return a capability dispatcher, or {@code null} if no capabilities
     *         or parent provider are available
     */
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
