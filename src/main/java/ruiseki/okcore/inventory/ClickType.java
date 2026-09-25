package ruiseki.okcore.inventory;

/**
 * Represents the different types of click interactions that can occur within a GUI container.
 * <p>
 * This enum maps 1:1 with the internal click modes used by Minecraft 1.7.10's
 * {@link net.minecraft.inventory.Container#slotClick(int, int, int, net.minecraft.entity.player.EntityPlayer)} method.
 * </p>
 *
 * @author ruiseki
 */
public enum ClickType {

    /**
     * Standard click with either left or right mouse button to pick up, place, or split an item stack.
     */
    PICKUP,

    /**
     * Shift-click interaction to quickly transfer items between container inventories.
     */
    QUICK_MOVE,

    /**
     * Hotbar swap action using number keys (1-9) to swap items between the hovered slot and hotbar.
     */
    SWAP,

    /**
     * Middle-click action in Creative mode to duplicate the target item stack.
     */
    CLONE,

    /**
     * Keypress action (e.g., 'Q') or clicking outside the container bounds to drop items onto the ground.
     */
    THROW,

    /**
     * Dragging operation across multiple slots to evenly distribute or split an item stack (Quick Craft).
     */
    QUICK_CRAFT,

    /**
     * Double-click interaction on a slot to collect all matching items into a single stack.
     */
    PICKUP_ALL;

    /**
     * Cached array of all enum values to prevent unnecessary array allocations during lookup.
     */
    public static final ClickType[] VALUES = values();

    /**
     * Retrieves the {@link ClickType} corresponding to the specified integer mode index.
     *
     * @param number The integer mode index (typically 0 through 6).
     * @return The matching {@link ClickType}, or {@link #PICKUP} if the index is out of bounds.
     */
    public static ClickType fromNumber(int number) {
        if (number < 0 || number >= VALUES.length) {
            return PICKUP;
        }
        return VALUES[number];
    }

    /**
     * Converts this {@link ClickType} into its corresponding Minecraft 1.7.10 internal mode integer.
     *
     * @return The zero-based integer ordinal matching the click mode.
     */
    public int toNumber() {
        return ordinal();
    }
}
