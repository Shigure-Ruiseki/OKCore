package ruiseki.okcore.tileentity;

import net.minecraft.world.World;
import ruiseki.okcore.enums.RedstoneMode;

/**
 * Interface to be implemented by TileEntities or blocks that possess configurable
 * Redstone behaviors (e.g., machines, automation devices).
 * <p>
 * Backported to Minecraft 1.7.10. Coordinates are managed using primitive ints.
 * </p>
 *
 * @author ruiseki
 */
public interface IRedstoneMode {

    /**
     * Gets the current Redstone configuration mode of this device.
     *
     * @return the active {@link RedstoneMode}
     */
    RedstoneMode getRedstoneMode();

    /**
     * Sets the Redstone configuration mode for this device.
     *
     * @param mode the new {@link RedstoneMode} to apply
     */
    void setRedstoneMode(RedstoneMode mode);

    /**
     * Toggles to the next Redstone mode in sequence.
     * Typically bound to Left-Click actions on a GUI button.
     */
    default void toggleRedstoneMode() {
        setRedstoneMode(getRedstoneMode().next());
    }

    /**
     * Toggles back to the previous Redstone mode in sequence.
     * Typically bound to Right-Click actions on a GUI button for better UX.
     */
    default void toggleRedstoneModeBackward() {
        setRedstoneMode(getRedstoneMode().prev());
    }

    /**
     * Checks if the device is permitted to run based on a given external power state.
     *
     * @param isPowered true if the block is currently receiving an in-world redstone signal
     * @return true if operational conditions are met; false otherwise
     */
    default boolean canRun(boolean isPowered) {
        return RedstoneMode.isActive(getRedstoneMode(), isPowered);
    }

    /**
     * Context-aware helper to directly evaluate if the device can run inside the world.
     * Automatically queries neighbors for Redstone signals using 1.7.10 physics methods.
     *
     * @param world the world the device resides in
     * @param x the X coordinate of the device
     * @param y the Y coordinate of the device
     * @param z the Z coordinate of the device
     * @return true if the device can execute its ticking logic; false otherwise
     */
    default boolean canRunInWorld(World world, int x, int y, int z) {
        if (world == null) {
            return false;
        }
        // isBlockIndirectlyGettingPowered checks if the block is receiving strong or weak redstone signal from any side
        boolean isPowered = world.isBlockIndirectlyGettingPowered(x, y, z);
        return canRun(isPowered);
    }

    /**
     * Decodes an integer index (usually received via Network Packets or NBT Data)
     * and safe-applies it as the new Redstone configuration mode.
     *
     * @param index the internal enum index to look up
     */
    default void setRedstoneModeByIndex(int index) {
        setRedstoneMode(RedstoneMode.byIndex(index));
    }
}
