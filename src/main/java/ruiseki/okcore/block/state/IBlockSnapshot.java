
package ruiseki.okcore.block.state;

import org.jetbrains.annotations.Nullable;

/**
 * Provides access to the extended block state captured by a
 * {@link net.minecraftforge.common.util.BlockSnapshot}.
 *
 * <p>
 * This interface is implemented through a mixin and allows
 * extended block states to be preserved independently of vanilla
 * block metadata during snapshot restoration.
 * </p>
 *
 * @see net.minecraftforge.common.util.BlockSnapshot
 * @see IBlockState
 */
public interface IBlockSnapshot {

    /**
     * Gets the extended block state captured by this snapshot.
     *
     * @return the captured extended block state, or {@code null}
     *         if no state was captured
     */
    @Nullable
    IBlockState okcore$getCapturedState();

    /**
     * Sets the extended block state associated with this snapshot.
     *
     * @param state the extended block state to associate with this snapshot,
     *              or {@code null} to clear it
     */
    void okcore$setCapturedState(@Nullable IBlockState state);
}
