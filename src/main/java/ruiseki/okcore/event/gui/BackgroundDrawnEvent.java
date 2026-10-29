package ruiseki.okcore.event.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.lwjgl.input.Mouse;

/**
 * Fired at the end of {@link GuiScreen#drawDefaultBackground()},
 * before the rest of the GUI is rendered.
 *
 * <p>
 * Allows drawing above the background but below GUI components
 * and tooltips.
 * </p>
 *
 * <p>
 * Provides the current mouse coordinates and scaled screen resolution.
 * </p>
 */
public class BackgroundDrawnEvent extends GuiScreenEvent {

    private final int mouseX;
    private final int mouseY;
    private final ScaledResolution scaledResolution;

    /**
     * Constructs a background rendering event for the specified GUI.
     *
     * @param gui the GUI being rendered
     */
    public BackgroundDrawnEvent(GuiScreen gui) {
        super(gui);

        final int displayWidth = gui.mc.displayWidth;
        final int displayHeight = gui.mc.displayHeight;

        this.scaledResolution = new ScaledResolution(gui.mc, displayWidth, displayHeight);

        final int scaledWidth = scaledResolution.getScaledWidth();
        final int scaledHeight = scaledResolution.getScaledHeight();

        this.mouseX = Mouse.getX() * scaledWidth / displayWidth;
        this.mouseY = scaledHeight - Mouse.getY() * scaledHeight / displayHeight - 1;
    }

    /**
     * Returns the mouse X coordinate in scaled GUI space.
     *
     * @return the scaled mouse X coordinate
     */
    public int getMouseX() {
        return mouseX;
    }

    /**
     * Returns the mouse Y coordinate in scaled GUI space.
     *
     * @return the scaled mouse Y coordinate
     */
    public int getMouseY() {
        return mouseY;
    }

    /**
     * Returns the scaled resolution used for GUI rendering.
     *
     * <p>
     * The resolution is calculated from the Minecraft display
     * dimensions and the current GUI scale setting.
     * </p>
     *
     * @return the scaled screen resolution
     */
    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
