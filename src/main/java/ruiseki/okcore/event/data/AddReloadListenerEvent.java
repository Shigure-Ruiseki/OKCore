
package ruiseki.okcore.event.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.Event;
import ruiseki.okcore.data.PreparableReloadListener;

/**
 * Fired when OKCore collects data reload listeners.
 *
 * <p>
 * This event allows mods to register {@link PreparableReloadListener}
 * instances that participate in the data reload lifecycle.
 * </p>
 *
 * <p>
 * Subscribe to this event through the Forge event bus and call
 * {@link #addListener(PreparableReloadListener)} to register additional
 * listeners.
 * </p>
 *
 * <p>
 * Registered listeners are stored in registration order.
 * The event exposes an immutable snapshot of the registered listeners
 * through {@link #getListeners()}.
 * </p>
 *
 * <p>
 * This event is not cancelable and does not have a result.
 * </p>
 *
 * @see PreparableReloadListener
 */
public class AddReloadListenerEvent extends Event {

    private final List<PreparableReloadListener> listeners = new ArrayList<>();

    /**
     * Constructs an event for collecting data reload listeners.
     */
    public AddReloadListenerEvent() {}

    /**
     * Registers a listener for the data reload lifecycle.
     *
     * <p>
     * Listeners are appended in the order they are registered.
     * Duplicate registrations are not filtered by this method.
     * </p>
     *
     * @param listener the reload listener to register
     */
    public void addListener(PreparableReloadListener listener) {
        listeners.add(listener);
    }

    /**
     * Returns an immutable snapshot of all registered reload listeners.
     *
     * <p>
     * The returned list preserves registration order. Modifying it
     * is not supported and does not affect the event's internal list.
     * </p>
     *
     * @return an immutable list of registered reload listeners
     */
    public List<PreparableReloadListener> getListeners() {
        return ImmutableList.copyOf(listeners);
    }
}
