
package ruiseki.okcore.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.ContainerExtended;

/**
 * Provides access to the {@link ContainerExtended} associated with a GUI screen.
 *
 * <p>
 * GUI implementations can use this interface to expose their underlying
 * inventory container in a type-safe manner.
 * </p>
 *
 * <p>
 * The generic type parameter allows callers to retrieve the concrete
 * container type without requiring an explicit cast.
 * </p>
 *
 * <p>
 * This interface is intended for client-side use only and can be used
 * with {@link ScreenFactorySafe} to construct GUI screens associated
 * with specific container types.
 * </p>
 *
 * @param <T> the type of container exposed by the implementation
 *
 * @see ContainerExtended
 * @see ScreenFactorySafe
 */
@SideOnly(Side.CLIENT)
public interface IContainerAccess<T extends ContainerExtended> {

    /**
     * Returns the inventory container associated with this GUI.
     *
     * @return the underlying container instance
     */
    T getContainer();
}
