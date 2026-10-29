
package ruiseki.okcore.event.generic;

import java.lang.reflect.Type;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Base implementation of a Forge event that carries generic type information.
 *
 * <p>
 * This class associates an event instance with a specific {@link Class},
 * allowing event handlers and dispatching mechanisms to distinguish
 * events by their generic type at runtime.
 * </p>
 *
 * <p>
 * Since Java erases generic type parameters at runtime, subclasses must
 * explicitly provide the corresponding {@link Class} when constructing
 * the event.
 * </p>
 *
 * <p>
 * The generic type is exposed through {@link #getGenericType()} and
 * can be used by event dispatchers that support generic event filtering.
 * </p>
 *
 * <p>
 * This class does not implement generic event filtering itself.
 * Filtering behavior depends on the event bus and its integration
 * with {@link IGenericEvent}.
 * </p>
 *
 * @param <T> the type associated with this event
 *
 * @see IGenericEvent
 * @see Event
 */
public class GenericEvent<T> extends Event implements IGenericEvent<T> {

    private Class<T> type;

    /**
     * Constructs a generic event associated with the specified type.
     *
     * <p>
     * Subclasses should pass the runtime class representing the
     * generic type parameter used by the event.
     * </p>
     *
     * @param type the runtime class associated with this event
     */
    protected GenericEvent(Class<T> type) {
        this.type = type;
    }

    /**
     * Returns the generic type associated with this event.
     *
     * <p>
     * The returned {@link Type} is the {@link Class} supplied
     * during construction.
     * </p>
     *
     * @return the generic type represented by this event
     */
    @Override
    public Type getGenericType() {
        return type;
    }
}
