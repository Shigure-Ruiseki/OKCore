
package ruiseki.okcore.inventory.slot;

import net.minecraft.util.ResourceLocation;

/**
 * Defines an interface for managing the background texture of an inventory slot.
 *
 * <p>
 * Implementations provide access to a {@link ResourceLocation} representing
 * the texture used to render the slot background.
 * </p>
 *
 * <p>
 * This interface only defines how the background texture is accessed
 * and modified. The actual rendering behavior is determined by the
 * GUI or slot implementation.
 * </p>
 *
 * @see ResourceLocation
 */
public interface ISlotBackground {

    /**
     * Returns the background texture associated with this slot.
     *
     * @return the resource location of the slot background texture
     */
    ResourceLocation getBackgroundTexture();

    /**
     * Sets the background texture associated with this slot.
     *
     * <p>
     * Implementations may use the supplied texture during subsequent
     * rendering operations.
     * </p>
     *
     * @param backgroundTexture the resource location of the background texture
     */
    void setBackgroundTexture(ResourceLocation backgroundTexture);
}
