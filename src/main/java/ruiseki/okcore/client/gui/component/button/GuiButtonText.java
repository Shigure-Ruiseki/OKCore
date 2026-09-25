package ruiseki.okcore.client.gui.component.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * An button with text.
 *
 * @author rubensworks
 *
 */
public class GuiButtonText extends GuiButtonExtended {

    /**
     * Make a new instance.
     *
     * @param x      X
     * @param y      Y
     * @param string The string to print.
     */
    public GuiButtonText(int x, int y, String string, OnPress onPress) {
        this(x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(string) + 6, 16, string, onPress, true);
    }

    /**
     * Make a new instance.
     *
     * @param x          X
     * @param y          Y
     * @param width      Width
     * @param height     Height
     * @param string     The string to print.
     * @param background If the button background should be rendered.
     */
    public GuiButtonText(int x, int y, int width, int height, String string, OnPress onPress, boolean background) {
        super(x, y, width, height, string, onPress, background);
    }

    @Override
    protected void drawButtonInner(int i, int j, boolean mouseOver) {
        FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;

        int color = 0xe0e0e0;
        if (!active) {
            color = 0xffa0a0a0;
        } else if (mouseOver) {
            color = 0xffffa0;
        }

        drawCenteredString(fontrenderer, narrationMessage, x + width / 2, y + (height - 8) / 2, color);
    }

}
