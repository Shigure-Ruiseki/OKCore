package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;

/**
 * Defines a custom renderable component within an item tooltip.
 *
 * <p>
 * Implementations provide their own sizing, layout, and rendering logic.
 * Unlike ordinary text lines, custom tooltip components can render
 * additional content such as icons, images, progress bars, or interactive
 * GUI elements.
 * </p>
 *
 * <p>
 * The tooltip renderer first determines the component's preferred width
 * using {@link #getWidth(FontRenderer)}. Returning {@code -1} indicates
 * that the component should use the available tooltip width.
 * </p>
 *
 * <p>
 * Once the allocated width is known, the renderer invokes
 * {@link #getHeight(FontRenderer, int)} to determine the required height.
 * This allows components to adapt their layout to the available space,
 * including text wrapping and dynamically sized content.
 * </p>
 *
 * <p>
 * Finally, {@link #draw(FontRenderer, int, int, int, int, int, int)}
 * renders the component within its allocated area.
 * </p>
 *
 * <p>
 * Implementations should ensure that their measured dimensions are
 * consistent with their rendering behavior to prevent overlapping
 * adjacent tooltip components.
 * </p>
 *
 * @see FontRenderer
 * @see TooltipLineHandler
 */
public interface ITooltipLine {

    /**
     * Returns the preferred horizontal space required by this component.
     *
     * <p>
     * Returning {@code -1} indicates that the component should use
     * the available tooltip width.
     * </p>
     *
     * @param font the font renderer used by the tooltip
     * @return the preferred width in pixels, or {@code -1} for automatic width
     */
    int getWidth(FontRenderer font);

    /**
     * Returns the vertical space required to render this component
     * within the specified allocated width.
     *
     * <p>
     * Implementations can use this value to calculate their layout,
     * including text wrapping and dynamically sized content.
     * </p>
     *
     * @param font  the font renderer used by the tooltip
     * @param width the allocated width in pixels
     * @return the required height in pixels
     */
    int getHeight(FontRenderer font, int width);

    /**
     * Renders this component within its allocated tooltip area.
     *
     * <p>
     * The coordinates {@code x} and {@code y} represent the top-left
     * corner of the component. Mouse coordinates are relative to
     * the GUI screen.
     * </p>
     *
     * <p>
     * The allocated dimensions should match those used when calculating
     * {@link #getHeight(FontRenderer, int)}.
     * </p>
     *
     * <p>
     * Implementations that modify OpenGL rendering state must restore
     * the previous state before returning.
     * </p>
     *
     * @param font   the font renderer used by the tooltip
     * @param x      the horizontal position of the component
     * @param y      the vertical position of the component
     * @param mouseX the horizontal position of the mouse cursor
     * @param mouseY the vertical position of the mouse cursor
     * @param width  the allocated width in pixels
     * @param height the allocated height in pixels
     */
    void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height);
}
