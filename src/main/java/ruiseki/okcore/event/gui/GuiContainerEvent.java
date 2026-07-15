package ruiseki.okcore.event.gui;

import net.minecraft.client.gui.inventory.GuiContainer;

import cpw.mods.fml.common.eventhandler.Event;

/**
 * Event class for handling GuiContainer specific events.
 */
public class GuiContainerEvent extends Event {

    private final GuiContainer guiContainer;

    public GuiContainerEvent(GuiContainer guiContainer) {
        this.guiContainer = guiContainer;
    }

    public GuiContainer getGuiContainer() {
        return guiContainer;
    }

    /**
     * This event is fired directly after the GuiContainer has draw any foreground elements,
     * But before the "dragged" stack, and before any tooltips.
     * This is useful for any slot / item specific overlays.
     * Things that need to be on top of All GUI elements but bellow tooltips and dragged stacks.
     */
    public static class DrawForeground extends GuiContainerEvent {

        private final int mouseX;
        private final int mouseY;

        /**
         * Called directly after the GuiContainer has drawn any foreground elements.
         *
         * @param guiContainer The container.
         * @param mouseX       The current X position of the players mouse.
         * @param mouseY       The current Y position of the players mouse.
         */
        public DrawForeground(GuiContainer guiContainer, int mouseX, int mouseY) {
            super(guiContainer);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public int getMouseX() {
            return mouseX;
        }

        public int getMouseY() {
            return mouseY;
        }
    }
}
