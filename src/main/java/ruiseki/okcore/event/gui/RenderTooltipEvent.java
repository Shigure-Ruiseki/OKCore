package ruiseki.okcore.event.gui;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Base event for customizing item tooltip rendering.
 *
 * <p>
 * Events are dispatched at different stages of tooltip rendering,
 * allowing subscribers to modify tooltip content, rendering parameters,
 * background colors, or draw additional content.
 * </p>
 *
 * <p>
 * The tooltip lines are exposed as a mutable list. However, modifications
 * to tooltip content should only be made during {@link Pre}, before tooltip
 * dimensions and layout are calculated.
 * </p>
 *
 * <p>
 * The supported rendering stages are:
 * </p>
 *
 * <ul>
 * <li>{@link Pre} - Before tooltip layout and rendering.</li>
 * <li>{@link Color} - When tooltip background colors are determined.</li>
 * <li>{@link PostBackground} - After the background is rendered, before text.</li>
 * <li>{@link PostText} - After tooltip text is rendered.</li>
 * </ul>
 *
 * <p>
 * These events are intended for client-side rendering only.
 * </p>
 *
 * @see Pre
 * @see Color
 * @see PostBackground
 * @see PostText
 */
public abstract class RenderTooltipEvent extends Event {

    @Nonnull
    protected final ItemStack stack;

    @Nonnull
    protected final List<String> lines;

    protected int x;
    protected int y;

    @Nonnull
    protected FontRenderer fr;

    /**
     * Constructs a tooltip rendering event.
     *
     * @param stack the item stack associated with the tooltip
     * @param lines the mutable tooltip text lines
     * @param x     the tooltip X position
     * @param y     the tooltip Y position
     * @param fr    the font renderer used for tooltip text
     */
    public RenderTooltipEvent(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y,
        @Nonnull FontRenderer fr) {
        this.stack = stack;
        this.lines = lines;
        this.x = x;
        this.y = y;
        this.fr = fr;
    }

    /**
     * Gets the item stack associated with this tooltip.
     *
     * <p>
     * The stack may be empty when rendering tooltips without an associated item.
     * </p>
     *
     * @return the associated item stack
     */
    @Nonnull
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Gets the tooltip text lines.
     *
     * <p>
     * The returned list is mutable. Subscribers may add, remove, or modify
     * entries. Changes should only be made during {@link Pre}, before tooltip
     * dimensions and layout are calculated.
     * </p>
     *
     * @return the mutable tooltip text lines
     */
    @Nonnull
    public List<String> getLines() {
        return lines;
    }

    /**
     * Gets the tooltip X position.
     *
     * @return the X position, initially based on the mouse position
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the tooltip Y position.
     *
     * @return the Y position, initially based on the mouse position
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the font renderer used for tooltip text.
     *
     * @return the current font renderer
     */
    @Nonnull
    public FontRenderer getFontRenderer() {
        return fr;
    }

    /**
     * Fired before tooltip dimensions and placement are calculated.
     *
     * <p>
     * Subscribers may modify tooltip content, position, maximum width,
     * screen dimensions, and font renderer before rendering begins.
     * </p>
     *
     * <p>
     * Canceling this event prevents the tooltip rendering operation
     * from continuing.
     * </p>
     */
    @Cancelable
    public static class Pre extends RenderTooltipEvent {

        private int screenWidth;
        private int screenHeight;
        private int maxWidth;

        /**
         * Constructs a pre-render tooltip event.
         *
         * @param stack        the item stack associated with the tooltip
         * @param lines        the mutable tooltip text lines
         * @param x            the initial tooltip X position
         * @param y            the initial tooltip Y position
         * @param screenWidth  the screen width
         * @param screenHeight the screen height
         * @param maxWidth     the maximum tooltip width, or {@code -1} for unlimited
         * @param fr           the font renderer used for tooltip text
         */
        public Pre(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, int screenWidth,
            int screenHeight, int maxWidth, @Nonnull FontRenderer fr) {
            super(stack, lines, x, y, fr);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.maxWidth = maxWidth;
        }

        /**
         * Gets the screen width.
         *
         * @return the screen width in pixels
         */
        public int getScreenWidth() {
            return screenWidth;
        }

        /**
         * Sets the screen width.
         *
         * @param screenWidth the new screen width in pixels
         */
        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        /**
         * Gets the screen height.
         *
         * @return the screen height in pixels
         */
        public int getScreenHeight() {
            return screenHeight;
        }

        /**
         * Sets the screen height.
         *
         * @param screenHeight the new screen height in pixels
         */
        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        /**
         * Gets the maximum tooltip width.
         *
         * @return the maximum width in pixels, or {@code -1} for unlimited
         */
        public int getMaxWidth() {
            return maxWidth;
        }

        /**
         * Sets the maximum tooltip width.
         *
         * @param maxWidth the maximum width in pixels, or {@code -1} for unlimited
         */
        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }

        /**
         * Sets the font renderer used for tooltip text.
         *
         * @param fr the font renderer to use
         */
        public void setFontRenderer(@Nonnull FontRenderer fr) {
            this.fr = fr;
        }

        /**
         * Sets the tooltip X position.
         *
         * @param x the new X position
         */
        public void setX(int x) {
            this.x = x;
        }

        /**
         * Sets the tooltip Y position.
         *
         * @param y the new Y position
         */
        public void setY(int y) {
            this.y = y;
        }
    }

    /**
     * Base class for events fired after the tooltip background or text
     * has been rendered.
     *
     * <p>
     * This class is not intended to be used directly. Use one of its
     * subclasses instead.
     * </p>
     *
     * @see PostBackground
     * @see PostText
     */
    protected static abstract class Post extends RenderTooltipEvent {

        private final int width;
        private final int height;

        /**
         * Constructs a post-render tooltip event.
         *
         * @param stack  the item stack associated with the tooltip
         * @param lines  the tooltip text lines
         * @param x      the tooltip X position
         * @param y      the tooltip Y position
         * @param fr     the font renderer used for tooltip text
         * @param width  the inner tooltip width, excluding the border
         * @param height the inner tooltip height, excluding the border
         */
        public Post(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, @Nonnull FontRenderer fr,
            int width, int height) {
            super(stack, lines, x, y, fr);
            this.width = width;
            this.height = height;
        }

        /**
         * Gets the inner tooltip width, excluding the border.
         *
         * @return the tooltip width in pixels
         */
        public int getWidth() {
            return width;
        }

        /**
         * Gets the inner tooltip height, excluding the border.
         *
         * @return the tooltip height in pixels
         */
        public int getHeight() {
            return height;
        }
    }

    /**
     * Fired immediately after the tooltip background is rendered,
     * before tooltip text is drawn.
     *
     * <p>
     * This event is useful for drawing custom backgrounds, decorations,
     * or other elements that should appear behind tooltip text.
     * </p>
     */
    public static class PostBackground extends Post {

        /**
         * Constructs a post-background tooltip event.
         *
         * @param stack  the item stack associated with the tooltip
         * @param lines  the tooltip text lines
         * @param x      the tooltip X position
         * @param y      the tooltip Y position
         * @param fr     the font renderer used for tooltip text
         * @param width  the inner tooltip width, excluding the border
         * @param height the inner tooltip height, excluding the border
         */
        public PostBackground(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y,
            @Nonnull FontRenderer fr, int width, int height) {
            super(stack, lines, x, y, fr, width, height);
        }
    }

    /**
     * Fired immediately after tooltip text is rendered,
     * before the tooltip renderer restores its OpenGL state.
     *
     * <p>
     * This event is useful for drawing additional text, icons,
     * or effects above the existing tooltip content.
     * </p>
     */
    public static class PostText extends Post {

        /**
         * Constructs a post-text tooltip event.
         *
         * @param stack  the item stack associated with the tooltip
         * @param lines  the tooltip text lines
         * @param x      the tooltip X position
         * @param y      the tooltip Y position
         * @param fr     the font renderer used for tooltip text
         * @param width  the inner tooltip width, excluding the border
         * @param height the inner tooltip height, excluding the border
         */
        public PostText(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, @Nonnull FontRenderer fr,
            int width, int height) {
            super(stack, lines, x, y, fr, width, height);
        }
    }

    /**
     * Fired when the tooltip background and border colors are determined.
     *
     * <p>
     * Subscribers may replace the background color, upper border gradient,
     * and lower border gradient colors.
     * </p>
     *
     * <p>
     * Original color values remain available through their corresponding
     * getters, even after the mutable values are changed.
     * </p>
     *
     * <p>
     * Colors are represented as packed ARGB integers.
     * </p>
     */
    public static class Color extends RenderTooltipEvent {

        private final int originalBackground;
        private final int originalBorderStart;
        private final int originalBorderEnd;

        private int background;
        private int borderStart;
        private int borderEnd;

        /**
         * Constructs a tooltip color event.
         *
         * @param stack       the item stack associated with the tooltip
         * @param lines       the tooltip text lines
         * @param x           the tooltip X position
         * @param y           the tooltip Y position
         * @param fr          the font renderer used for tooltip text
         * @param background  the initial background color
         * @param borderStart the initial upper border gradient color
         * @param borderEnd   the initial lower border gradient color
         */
        public Color(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, @Nonnull FontRenderer fr,
            int background, int borderStart, int borderEnd) {
            super(stack, lines, x, y, fr);

            this.originalBackground = background;
            this.originalBorderStart = borderStart;
            this.originalBorderEnd = borderEnd;

            this.background = background;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        /**
         * Gets the current tooltip background color.
         *
         * @return the background color as a packed ARGB integer
         */
        public int getBackground() {
            return background;
        }

        /**
         * Sets the tooltip background color.
         *
         * @param background the new background color as a packed ARGB integer
         */
        public void setBackground(int background) {
            this.background = background;
        }

        /**
         * Gets the current upper border gradient color.
         *
         * @return the upper border color as a packed ARGB integer
         */
        public int getBorderStart() {
            return borderStart;
        }

        /**
         * Sets the upper border gradient color.
         *
         * @param borderStart the new upper border color as a packed ARGB integer
         */
        public void setBorderStart(int borderStart) {
            this.borderStart = borderStart;
        }

        /**
         * Gets the current lower border gradient color.
         *
         * @return the lower border color as a packed ARGB integer
         */
        public int getBorderEnd() {
            return borderEnd;
        }

        /**
         * Sets the lower border gradient color.
         *
         * @param borderEnd the new lower border color as a packed ARGB integer
         */
        public void setBorderEnd(int borderEnd) {
            this.borderEnd = borderEnd;
        }

        /**
         * Gets the original tooltip background color.
         *
         * @return the original background color as a packed ARGB integer
         */
        public int getOriginalBackground() {
            return originalBackground;
        }

        /**
         * Gets the original upper border gradient color.
         *
         * @return the original upper border color as a packed ARGB integer
         */
        public int getOriginalBorderStart() {
            return originalBorderStart;
        }

        /**
         * Gets the original lower border gradient color.
         *
         * @return the original lower border color as a packed ARGB integer
         */
        public int getOriginalBorderEnd() {
            return originalBorderEnd;
        }
    }
}
