
package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;

/**
 * Defines a custom renderable line within an item tooltip.
 *
 * <p>
 * Implementations provide the dimensions and rendering logic for
 * a tooltip component. Unlike ordinary text lines, custom tooltip
 * lines can render additional content such as icons, images,
 * progress bars, or other GUI elements.
 * </p>
 *
 * <p>
 * The tooltip renderer uses {@link #getWidth(FontRenderer)} and
 * {@link #getHeight(FontRenderer)} to determine the space required
 * by the component before invoking {@link #draw(FontRenderer, int, int)}.
 * </p>
 *
 * <p>
 * Implementations should report dimensions consistent with their
 * rendering behavior to avoid overlapping adjacent tooltip content.
 * </p>
 *
 * @see FontRenderer
 * @see TooltipLineHandler
 */
public interface ITooltipLine {

    /**
     * Returns the horizontal space required to render this tooltip line.
     *
     * @param font the font renderer used by the tooltip
     * @return the width of the tooltip line in pixels
     */
    int getWidth(FontRenderer font);

    /**
     * Returns the vertical space required to render this tooltip line.
     *
     * @param font the font renderer used by the tooltip
     * @return the height of the tooltip line in pixels
     */
    int getHeight(FontRenderer font);

    /**
     * Renders this tooltip line at the specified position.
     *
     * <p>
     * The coordinates identify the origin of the tooltip component
     * in the active GUI coordinate system.
     * </p>
     *
     * <p>
     * Implementations that modify OpenGL rendering state should restore
     * it before returning to avoid affecting subsequent tooltip content.
     * </p>
     *
     * @param font the font renderer used by the tooltip
     * @param x    the horizontal rendering position
     * @param y    the vertical rendering position
     */
    void draw(FontRenderer font, int x, int y);
}
