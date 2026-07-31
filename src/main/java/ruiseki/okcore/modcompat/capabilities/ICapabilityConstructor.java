package ruiseki.okcore.modcompat.capabilities;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * Constructor for capabilities.
 * 
 * @param <C> The capability type
 * @param <T> The host type
 * @param <H> The host that will contain the capability.
 * @author rubensworks
 */
public interface ICapabilityConstructor<C, T, H> extends ICapabilityTypeGetter<C> {

    /**
     * @param hostType The host type for capabilities.
     * @param host     The host for capabilities
     * @return A new capability provider for the given host.
     */
    public @Nullable ICapabilityProvider createProvider(T hostType, H host);

}
