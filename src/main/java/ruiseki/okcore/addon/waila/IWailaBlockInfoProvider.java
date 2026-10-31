
package ruiseki.okcore.addon.waila;

import java.util.List;

import net.minecraft.item.ItemStack;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

/**
 * Provides custom WAILA tooltip information for a block or tile entity.
 *
 * <p>
 * Implementations may override individual callbacks to customize the
 * displayed item stack, header, body, or footer.
 *
 * <p>
 * All tooltip callbacks are invoked on the client. Server-side data
 * should be provided through {@link IWailaNBTProvider}.
 *
 * <p>
 * Default implementations do not modify the tooltip.
 *
 * @see IWailaTileInfoProvider
 * @see IWailaNBTProvider
 */
public interface IWailaBlockInfoProvider {

    /**
     * Returns a custom item stack for the WAILA display.
     *
     * @param accessor the WAILA data accessor
     * @param config   the WAILA configuration
     * @return the custom stack, or {@code null} to use another provider
     */
    default ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    /**
     * Adds information to the tooltip header.
     *
     * @param itemStack the displayed item stack
     * @param tooltip   the mutable tooltip lines
     * @param accessor  the WAILA data accessor
     * @param config    the WAILA configuration
     */
    default void getWailaHead(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {}

    /**
     * Adds information to the tooltip body.
     *
     * @param tooltip   the mutable tooltip lines
     * @param itemStack the displayed item stack
     * @param accessor  the WAILA data accessor
     * @param config    the WAILA configuration
     */
    default void getWailaBody(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {}

    /**
     * Adds information to the tooltip footer.
     *
     * @param itemStack the displayed item stack
     * @param tooltip   the mutable tooltip lines
     * @param accessor  the WAILA data accessor
     * @param config    the WAILA configuration
     */
    default void getWailaTail(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {}
}
