
package ruiseki.okcore.addon.waila;

/**
 * Combines WAILA tooltip customization and server-side NBT synchronization
 * for tile entities.
 *
 * <p>
 * Implementations inherit the tooltip callbacks from
 * {@link IWailaBlockInfoProvider} and the NBT synchronization callback from
 * {@link IWailaNBTProvider}.
 *
 * <p>
 * All callbacks are optional and provide default implementations.
 *
 * @see IWailaBlockInfoProvider
 * @see IWailaNBTProvider
 */
public interface IWailaTileInfoProvider extends IWailaBlockInfoProvider, IWailaNBTProvider {
}
