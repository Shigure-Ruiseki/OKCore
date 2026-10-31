package ruiseki.okcore.event.gui;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

/**
 * Base event for customizing item tooltip rendering.
 *
 * <p>
 * Events are dispatched at different stages of tooltip rendering,
 * allowing subscribers to modify rendering parameters, customize
 * tooltip colors, or draw additional content.
 * </p>
 *
 * <p>
 * Tooltip text is exposed as an unmodifiable list.
 * To modify the text itself, use {@link ItemTooltipEvent}.
 * </p>
 *
 * <p>
 * The supported rendering stages are:
 * </p>
 *
 * <ul>
 * <li>{@link Pre} - Before tooltip layout and rendering.</li>
 * <li>{@link Color} - When tooltip background colors are determined.</li>
 * <li>{@link PostBackground} - After the background is rendered.</li>
 * <li>{@link PostText} - After tooltip text is rendered.</li>
 * </ul>
 *
 * <p>
 * These events are intended for client-side rendering only.
 * </p>
 *
 * @see ItemTooltipEvent
 * @see Pre
 * @see Color
 * @see PostBackground
 * @see PostText
 */
public abstract class RenderTooltipEvent extends Event {

    @Nonnull
    protected final ItemStack stack;
    protected final List<String> lines;
    protected int x;
    protected int y;
    protected FontRenderer fr;

    /**
     * Constructs a tooltip rendering event.
     *
     * @param stack the item stack associated with the tooltip
     * @param lines the tooltip text lines
     * @param x     the tooltip X position
     * @param y     the tooltip Y position
     * @param fr    the font renderer used for tooltip text
     */
    public RenderTooltipEvent(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y,
        @Nonnull FontRenderer fr) {
        this.stack = stack;
        this.lines = Collections.unmodifiableList(lines); // Leave editing to ItemTooltipEvent
        this.x = x;
        this.y = y;
        this.fr = fr;
    }

    /**
     * @return The stack which the tooltip is being rendered for. As tooltips can be drawn without itemstacks, this
     *         stack may be empty.
     */
    @Nonnull
    public ItemStack getStack() {
        return stack;
    }

    /**
     * The lines to be drawn. May change between {@link RenderTooltipEvent.Pre} and {@link RenderTooltipEvent.Post}.
     *
     * @return An <i>unmodifiable</i> list of strings. Use {@link ItemTooltipEvent} to modify tooltip text.
     */
    @Nonnull
    public List<String> getLines() {
        return lines;
    }

    /**
     * @return The X position of the tooltip box. By default, the mouse X position.
     */
    public int getX() {
        return x;
    }

    /**
     * @return The Y position of the tooltip box. By default, the mouse Y position.
     */
    public int getY() {
        return y;
    }

    /**
     * @return The {@link FontRenderer} instance the current render is using.
     */
    @Nonnull
    public FontRenderer getFontRenderer() {
        return fr;
    }

    /**
     * This event is fired before any tooltip calculations are done. It provides setters for all aspects of the tooltip,
     * so the final render can be modified.
     * <p>
     * This event is {@link Cancelable}.
     */
    @Cancelable
    public static class Pre extends RenderTooltipEvent {

        private int screenWidth;
        private int screenHeight;
        private int maxWidth;

        /**
         * Fired before tooltip dimensions and placement are calculated.
         *
         * <p>
         * Subscribers may change the tooltip position, maximum width,
         * screen dimensions, and font renderer before rendering begins.
         * </p>
         *
         * <p>
         * Canceling this event prevents the tooltip rendering operation
         * from continuing.
         * </p>
         */
        public Pre(@Nonnull ItemStack stack, @Nonnull List<String> lines, int x, int y, int screenWidth,
            int screenHeight, int maxWidth, @Nonnull FontRenderer fr) {
            super(stack, lines, x, y, fr);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            this.maxWidth = maxWidth;
        }

        public int getScreenWidth() {
            return screenWidth;
        }

        public void setScreenWidth(int screenWidth) {
            this.screenWidth = screenWidth;
        }

        public int getScreenHeight() {
            return screenHeight;
        }

        public void setScreenHeight(int screenHeight) {
            this.screenHeight = screenHeight;
        }

        /**
         * @return The max width the tooltip can be. Defaults to -1 (unlimited).
         */
        public int getMaxWidth() {
            return maxWidth;
        }

        /**
         * Sets the max width of the tooltip. Use -1 for unlimited.
         */
        public void setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
        }

        /**
         * Sets the {@link FontRenderer} to be used to render text.
         */
        public void setFontRenderer(@Nonnull FontRenderer fr) {
            this.fr = fr;
        }

        /**
         * Set the X origin of the tooltip.
         */
        public void setX(int x) {
            this.x = x;
        }

        /**
         * Set the Y origin of the tooltip.
         */
        public void setY(int y) {
            this.y = y;
        }
    }

    /**
     * Events inheriting from this class are fired at different stages during the tooltip rendering.
     * <p>
     * Do not use this event directly, use one of its subclasses:
     * <ul>
     * <li>{@link RenderTooltipEvent.PostBackground}</li>
     * <li>{@link RenderTooltipEvent.PostText}</li>
     * </ul>
     */
    protected static abstract class Post extends RenderTooltipEvent {

        private final int width;
        private final int height;

        public Post(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr,
            int width, int height) {
            super(stack, textLines, x, y, fr);
            this.width = width;
            this.height = height;
        }

        /**
         * @return The width of the tooltip box. This is the width of the <i>inner</i> box, not including the border.
         */
        public int getWidth() {
            return width;
        }

        /**
         * @return The height of the tooltip box. This is the height of the <i>inner</i> box, not including the border.
         */
        public int getHeight() {
            return height;
        }
    }

    /**
     * This event is fired directly after the tooltip background is drawn, but before any text is drawn.
     */
    public static class PostBackground extends Post {

        /**
         * Fired immediately after the tooltip background is rendered,
         * before tooltip text is drawn.
         *
         * <p>
         * This event is useful for drawing custom backgrounds, decorations,
         * or other elements that should appear behind tooltip text.
         * </p>
         */
        public PostBackground(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y,
            @Nonnull FontRenderer fr, int width, int height) {
            super(stack, textLines, x, y, fr, width, height);
        }
    }

    /**
     * This event is fired directly after the tooltip text is drawn, but before the GL state is reset.
     */
    public static class PostText extends Post {

        /**
         * Fired immediately after tooltip text is rendered,
         * before the tooltip renderer restores its OpenGL state.
         *
         * <p>
         * This event is useful for drawing additional text, icons,
         * or effects above the existing tooltip content.
         * </p>
         */
        public PostText(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y,
            @Nonnull FontRenderer fr, int width, int height) {
            super(stack, textLines, x, y, fr, width, height);
        }
    }

    /**
     * This event is fired when the colours for the tooltip background are determined.
     */
    public static class Color extends RenderTooltipEvent {

        private final int originalBackground;
        private final int originalBorderStart;
        private final int originalBorderEnd;
        private int background;
        private int borderStart;
        private int borderEnd;

        /**
         * Fired when the tooltip background and border colors are determined.
         *
         * <p>
         * Subscribers may replace the background, upper border gradient,
         * and lower border gradient colors.
         * </p>
         *
         * <p>
         * The original color values remain available through their
         * corresponding getters, even after the mutable values are changed.
         * </p>
         *
         * <p>
         * Colors are represented as packed ARGB integers.
         * </p>
         */
        public Color(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer fr,
            int background, int borderStart, int borderEnd) {
            super(stack, textLines, x, y, fr);
            this.originalBackground = background;
            this.originalBorderStart = borderStart;
            this.originalBorderEnd = borderEnd;
            this.background = background;
            this.borderStart = borderStart;
            this.borderEnd = borderEnd;
        }

        public int getBackground() {
            return background;
        }

        public void setBackground(int background) {
            this.background = background;
        }

        public int getBorderStart() {
            return borderStart;
        }

        public void setBorderStart(int borderStart) {
            this.borderStart = borderStart;
        }

        public int getBorderEnd() {
            return borderEnd;
        }

        public void setBorderEnd(int borderEnd) {
            this.borderEnd = borderEnd;
        }

        public int getOriginalBackground() {
            return originalBackground;
        }

        public int getOriginalBorderStart() {
            return originalBorderStart;
        }

        public int getOriginalBorderEnd() {
            return originalBorderEnd;
        }
    }
}
