
package ruiseki.okcore.event.gui;

import net.minecraft.inventory.Slot;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Base event fired during inventory slot rendering.
 *
 * <p>
 * Provides access to the rendered {@link Slot} and its rendering coordinates.
 * Coordinates are relative to the active rendering context and may differ
 * from {@link Slot#xDisplayPosition} and {@link Slot#yDisplayPosition}.
 * </p>
 *
 * <p>
 * This event is intended for client-side rendering only and does not
 * modify inventory contents or slot interaction behavior.
 * </p>
 *
 * <p>
 * Supported rendering phases:
 * </p>
 * <ul>
 * <li>{@link Pre} - Fired before rendering and may be canceled.</li>
 * <li>{@link Post} - Fired after rendering for additional visual effects.</li>
 * </ul>
 *
 * @see Slot
 * @see Pre
 * @see Post
 * @see ISlotRenderPosition
 */
public abstract class SlotRenderEvent extends Event {

    private final Slot slot;
    private final int slotX;
    private final int slotY;

    /**
     * Constructs a slot rendering event.
     *
     * @param slot  the slot being rendered
     * @param slotX the horizontal position in the active rendering context
     * @param slotY the vertical position in the active rendering context
     */
    protected SlotRenderEvent(Slot slot, int slotX, int slotY) {
        this.slot = slot;
        this.slotX = slotX;
        this.slotY = slotY;
    }

    /**
     * Returns the slot associated with this event.
     *
     * @return the rendered slot
     */
    public Slot getSlot() {
        return slot;
    }

    /**
     * Returns the horizontal rendering position of the slot.
     *
     * @return the X coordinate in the active rendering context
     */
    public int getSlotX() {
        return slotX;
    }

    /**
     * Returns the vertical rendering position of the slot.
     *
     * @return the Y coordinate in the active rendering context
     */
    public int getSlotY() {
        return slotY;
    }

    /**
     * Fired before an inventory slot is rendered.
     *
     * <p>
     * This event is {@link Cancelable}. Canceling it allows the
     * dispatcher to skip the associated slot rendering operation.
     * </p>
     *
     * <p>
     * Cancellation does not necessarily suppress other rendering
     * operations, such as hover highlights, tooltips, or overlays.
     * </p>
     */
    @Cancelable
    public static class Pre extends SlotRenderEvent {

        /**
         * Constructs a pre-render event.
         *
         * @param slot  the slot about to be rendered
         * @param slotX the horizontal rendering position
         * @param slotY the vertical rendering position
         */
        public Pre(Slot slot, int slotX, int slotY) {
            super(slot, slotX, slotY);
        }
    }

    /**
     * Fired after an inventory slot has been rendered.
     *
     * <p>
     * This event is not cancelable and is intended for rendering
     * additional decorations, indicators, or overlays.
     * </p>
     *
     * <p>
     * The actual rendering order depends on the dispatcher.
     * Subsequent rendering operations may still affect the
     * final appearance of the slot.
     * </p>
     *
     * <p>
     * If {@link Pre} is canceled, dispatching this event depends
     * on the implementation of the rendering hook.
     * </p>
     */
    public static class Post extends SlotRenderEvent {

        /**
         * Constructs a post-render event.
         *
         * @param slot  the slot that has been rendered
         * @param slotX the horizontal rendering position
         * @param slotY the vertical rendering position
         */
        public Post(Slot slot, int slotX, int slotY) {
            super(slot, slotX, slotY);
        }
    }

    /**
     * Provides the rendering coordinates of an inventory slot.
     *
     * <p>
     * Implementations may use custom positioning logic, such as
     * centering a 16x16 item within a resizable slot widget.
     * </p>
     *
     * <p>
     * Coordinates must be expressed in the rendering context
     * used by the corresponding slot renderer.
     * </p>
     */
    public interface ISlotRenderPosition {

        /**
         * Returns the horizontal position used to render the slot content.
         *
         * @return the X rendering coordinate
         */
        int getSlotX();

        /**
         * Returns the vertical position used to render the slot content.
         *
         * @return the Y rendering coordinate
         */
        int getSlotY();
    }
}
