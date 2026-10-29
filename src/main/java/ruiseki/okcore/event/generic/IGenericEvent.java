
package ruiseki.okcore.event.generic;

import java.lang.reflect.Type;

/**
 * Defines a contract for events that expose their generic type at runtime.
 *
 * <p>
 * Implementations provide explicit type information that can be used
 * by event dispatchers to distinguish or filter events according to
 * their generic type.
 * </p>
 *
 * <p>
 * This interface is useful when Java's generic type erasure would
 * otherwise prevent the event system from identifying the type
 * parameter associated with an event instance.
 * </p>
 *
 * <p>
 * The returned type may be any implementation of {@link Type},
 * including {@link Class} or {@link java.lang.reflect.ParameterizedType}.
 * The exact representation depends on the implementation.
 * </p>
 *
 * @param <T> the generic type associated with the event
 *
 * @see GenericEvent
 * @see Type
 */
public interface IGenericEvent<T> {

    /**
     * Returns the runtime type associated with this event.
     *
     * <p>
     * Event dispatchers may use this information to perform
     * generic type matching or filtering.
     * </p>
     *
     * @return the generic type represented by this event
     */
    Type getGenericType();
}
