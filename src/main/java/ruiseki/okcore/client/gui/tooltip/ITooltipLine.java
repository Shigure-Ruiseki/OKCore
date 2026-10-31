package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;

/**
 * Defines a custom renderable component within an item tooltip.
 *
 * <p>
 * Implementations provide their own dimensions and rendering logic.
 * Unlike ordinary text lines, custom tooltip components can render
 * additional content such as icons, images, progress bars, or other
 * GUI elements.
 * </p>
 *
 * <p>
 * The tooltip renderer uses {@link #getWidth(FontRenderer)} and
 * {@link #getHeight(FontRenderer)} to determine the space required
 * by the component before invoking
 * {@link #draw(FontRenderer, int, int, int, int, int, int)}.
 * </p>
 *
 * <p>
 * Implementations should report dimensions consistent with their
 * rendering behavior to prevent overlapping adjacent tooltip content.
 * </p>
 *
 * <p>
 * The rendering method receives the mouse position and the allocated
 * component dimensions, allowing implementations to support interactive
 * rendering and adapt their layout to the available space.
 * </p>
 *
 * @see FontRenderer
 * @see TooltipLineHandler
 */
public interface ITooltipLine {

    /**
     * Returns the horizontal space required to render this component.
     *
     * <p>
     * Returning {@code -1} indicates that the component should use
     * the available tooltip width.
     * </p>
     *
     * @param font the font renderer used by the tooltip
     * @return the required width in pixels, or {@code -1} to use
     *         the available tooltip width
     */
    int getWidth(FontRenderer font);

    /**
     * Returns the vertical space required to render this component.
     *
     * @param font the font renderer used by the tooltip
     * @return the required height in pixels
     */
    int getHeight(FontRenderer font);

    /**
     * Renders this component within the allocated tooltip area.
     *
     * <p>
     * The coordinates {@code x} and {@code y} represent the top-left
     * corner of the component. The mouse coordinates are relative to
     * the GUI screen, allowing implementations to detect hover states
     * and handle mouse-dependent rendering.
     * </p>
     *
     * <p>
     * The {@code width} and {@code height} parameters represent the
     * dimensions allocated to this component by the tooltip renderer.
     * </p>
     *
     * <p>
     * Implementations that modify OpenGL rendering state must restore
     * the previous state before returning to avoid affecting subsequent
     * tooltip components.
     * </p>
     *
     * @param font   the font renderer used by the tooltip
     * @param x      the horizontal position of the component
     * @param y      the vertical position of the component
     * @param mouseX the horizontal position of the mouse cursor
     * @param mouseY the vertical position of the mouse cursor
     * @param width  the allocated width of the component in pixels
     * @param height the allocated height of the component in pixels
     */
    void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height);
}
