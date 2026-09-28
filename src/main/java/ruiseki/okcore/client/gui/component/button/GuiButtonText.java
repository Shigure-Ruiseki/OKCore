package ruiseki.okcore.client.gui.component.button;

import net.minecraft.client.Minecraft;

/**
 * An button with text.
 *
 * @author rubensworks
 *
 */
public class GuiButtonText extends GuiButtonExtended {

    private final String text;

    /**
     * Make a new instance.
     *
     * @param x    X
     * @param y    Y
     * @param text The string to print.
     */
    public GuiButtonText(int x, int y, String message, String text, OnPress onPress) {
        this(x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 6, 16, message, text, onPress, true);
    }

    /**
     * Make a new instance.
     *
     * @param x          X
     * @param y          Y
     * @param width      Width
     * @param height     Height
     * @param text       The string to print.
     * @param background If the button background should be rendered.
     */
    public GuiButtonText(int x, int y, int width, int height, String message, String text, OnPress onPress,
        boolean background) {
        super(x, y, width, height, message, onPress, background);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    protected void drawButtonInner(int i, int j, boolean mouseOver) {
        int color = 0xe0e0e0;
        if (!active) {
            color = 0xffa0a0a0;
        } else if (isHoveredOrFocused()) {
            color = 0xffffa0;
        }

        drawCenteredString(
            Minecraft.getMinecraft().fontRenderer,
            getText(),
            getX() + width / 2,
            getY() + (height - 8) / 2,
            color);
    }

}
