package ruiseki.okcore.event.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.GuiScreenEvent;

import org.lwjgl.input.Mouse;

/**
 * This event fires at the end of {@link GuiScreen#drawDefaultBackground()} and before the rest of the Gui draws.
 * This allows drawing next to Guis, above the background but below any tooltips.
 */
public class BackgroundDrawnEvent extends GuiScreenEvent {

    private final int mouseX;
    private final int mouseY;

    public BackgroundDrawnEvent(GuiScreen gui) {
        super(gui);
        final int displayWidth = gui.mc.displayWidth;
        final int displayHeight = gui.mc.displayHeight;
        final ScaledResolution scaledresolution = new ScaledResolution(gui.mc, displayWidth, displayHeight);
        final int scaledWidth = scaledresolution.getScaledWidth();
        final int scaledHeight = scaledresolution.getScaledHeight();
        this.mouseX = Mouse.getX() * scaledWidth / displayWidth;
        this.mouseY = scaledHeight - Mouse.getY() * scaledHeight / displayHeight - 1;
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
