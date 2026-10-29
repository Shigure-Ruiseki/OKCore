
package ruiseki.okcore.event.gui;

import net.minecraft.inventory.Slot;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Base event fired during the rendering of an inventory slot.
 *
 * <p>
 * This event allows other mods to observe or customize slot rendering
 * through the Minecraft Forge event bus.
 * </p>
 *
 * <p>
 * All events are client-side only and provide access to the
 * {@link Slot} currently being rendered.
 * </p>
 *
 * <p>
 * Event phases:
 * <ul>
 * <li>{@link Pre} - Fired before the slot is rendered. Can be canceled
 * to skip the original rendering operation.</li>
 * <li>{@link Post} - Fired after the slot rendering operation.</li>
 * </ul>
 * </p>
 *
 * <p>
 * These events only affect rendering and do not modify inventory
 * contents or slot interaction behavior.
 * </p>
 *
 * @see Slot
 * @see Pre
 * @see Post
 */
public abstract class SlotRenderEvent extends Event {

    private final Slot slot;

    /**
     * Constructs a new slot rendering event.
     *
     * @param slot the slot being rendered
     */
    protected SlotRenderEvent(Slot slot) {
        this.slot = slot;
    }

    /**
     * Returns the slot associated with this rendering event.
     *
     * @return the slot being rendered
     */
    public Slot getSlot() {
        return slot;
    }

    /**
     * Fired before a slot is rendered.
     *
     * <p>
     * This event is {@link Cancelable}. Canceling it prevents the
     * original slot rendering operation from executing.
     * </p>
     *
     * <p>
     * Subscribers may use this event to suppress vanilla slot rendering
     * or draw custom slot content.
     * </p>
     *
     * <p>
     * Canceling this event does not automatically prevent other
     * rendering operations, such as slot hover highlights or tooltips,
     * from executing.
     * </p>
     */
    @Cancelable
    public static class Pre extends SlotRenderEvent {

        /**
         * Constructs a pre-render event.
         *
         * @param slot the slot about to be rendered
         */
        public Pre(Slot slot) {
            super(slot);
        }
    }

    /**
     * Fired after a slot has completed its rendering operation.
     *
     * <p>
     * This event cannot be canceled and is intended for additional
     * rendering effects, decorations, or overlays.
     * </p>
     *
     * <p>
     * The exact rendering order depends on the injection point used
     * by the event dispatcher. Rendering operations performed after
     * this event may still affect the final appearance of the slot.
     * </p>
     *
     * <p>
     * If the pre-render event is canceled, whether this event is
     * dispatched depends on the implementation of the rendering hook.
     * </p>
     */
    public static class Post extends SlotRenderEvent {

        /**
         * Constructs a post-render event.
         *
         * @param slot the slot that has finished rendering
         */
        public Post(Slot slot) {
            super(slot);
        }
    }
}
