
package ruiseki.okcore.event.gui;

import net.minecraft.client.gui.inventory.GuiContainer;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Base event for rendering-related operations in a {@link GuiContainer}.
 *
 * <p>
 * Provides access to the container GUI associated with the event.
 * Subclasses represent specific stages of the GUI rendering lifecycle.
 * </p>
 *
 * <p>
 * These events are client-side only and are dispatched through the
 * Forge event bus.
 * </p>
 *
 * @see DrawForeground
 */
public class GuiContainerEvent extends Event {

    private final GuiContainer guiContainer;

    /**
     * Constructs an event associated with the specified container GUI.
     *
     * @param guiContainer the container GUI being rendered
     */
    public GuiContainerEvent(GuiContainer guiContainer) {
        this.guiContainer = guiContainer;
    }

    /**
     * Returns the container GUI associated with this event.
     *
     * @return the current container GUI
     */
    public GuiContainer getGuiContainer() {
        return guiContainer;
    }

    /**
     * Fired after the container foreground layer has been rendered,
     * but before the carried item stack and tooltips are drawn.
     *
     * <p>
     * This event is useful for rendering additional GUI decorations,
     * item indicators, and overlays above the container's foreground.
     * </p>
     *
     * <p>
     * Rendering performed during this event occurs before the carried
     * item stack and tooltip rendering stages, allowing those elements
     * to appear above custom overlays.
     * </p>
     *
     * <p>
     * Mouse coordinates are provided in scaled screen coordinates.
     * The current rendering matrix may still contain the container
     * translation, so renderers should account for the active
     * coordinate system.
     * </p>
     *
     * <p>
     * This event is not cancelable.
     * </p>
     */
    public static class DrawForeground extends GuiContainerEvent {

        private final int mouseX;
        private final int mouseY;

        /**
         * Constructs a foreground rendering event.
         *
         * @param guiContainer the container GUI being rendered
         * @param mouseX       the mouse X coordinate in scaled screen space
         * @param mouseY       the mouse Y coordinate in scaled screen space
         */
        public DrawForeground(GuiContainer guiContainer, int mouseX, int mouseY) {
            super(guiContainer);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        /**
         * Returns the mouse X coordinate.
         *
         * @return the mouse X coordinate in scaled screen space
         */
        public int getMouseX() {
            return mouseX;
        }

        /**
         * Returns the mouse Y coordinate.
         *
         * @return the mouse Y coordinate in scaled screen space
         */
        public int getMouseY() {
            return mouseY;
        }
    }
}
