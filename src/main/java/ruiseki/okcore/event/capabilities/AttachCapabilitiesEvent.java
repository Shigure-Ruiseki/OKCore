package ruiseki.okcore.event.capabilities;

import java.util.Collections;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Maps;

import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.event.generic.GenericEvent;

/**
 * Fired whenever an object with Capabilities support {currently TileEntity/Item/Entity)
 * is created. Allowing for the attachment of arbitrary capability providers.
 * <p>
 * Please note that as this is fired for ALL object creations efficient code is recommended.
 * And if possible use one of the subclasses to filter your intended objects.
 */
public class AttachCapabilitiesEvent<T> extends GenericEvent<T> {

    private final T obj;
    private final Map<ResourceLocation, ICapabilityProvider> caps = Maps.newLinkedHashMap();
    private final Map<ResourceLocation, ICapabilityProvider> view = Collections.unmodifiableMap(caps);

    public AttachCapabilitiesEvent(Class<T> type, T obj) {
        super(type);
        this.obj = obj;
    }

    public T getObject() {
        return obj;
    }

    /**
     * Adds a capability to be attached to this object.
     * Keys MUST be unique, it is suggested that you set the domain to your mod ID.
     * If the capability is an instance of INBTSerializable, this key will be used when serializing this capability.
     *
     * @param key The name of owner of this capability provider.
     * @param cap The capability provider
     */
    public void addCapability(ResourceLocation key, ICapabilityProvider cap) {
        if (caps.containsKey(key)) throw new IllegalStateException("Duplicate Capability Key: " + key + " " + cap);
        this.caps.put(key, cap);
    }

    /**
     * A unmodifiable view of the capabilities that will be attached to this object.
     */
    public Map<ResourceLocation, ICapabilityProvider> getCapabilities() {
        return view;
    }
}
