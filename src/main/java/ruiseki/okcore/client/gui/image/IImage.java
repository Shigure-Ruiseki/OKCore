
package ruiseki.okcore.client.gui.image;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * Represents a renderable image with intrinsic dimensions.
 *
 * <p>
 * Images can be rendered at their original dimensions, scaled to
 * custom dimensions, or tinted using RGBA color components.
 * </p>
 *
 * <p>
 * Rendering uses the currently active OpenGL coordinate system
 * and does not require a {@link Gui} instance.
 * </p>
 */
public interface IImage {

    /*
     * GUI RENDERING
     */

    /**
     * Draws the image at its original dimensions.
     *
     * @param x the horizontal position
     * @param y the vertical position
     */
    default void draw(int x, int y) {
        draw(x, y, getWidth(), getHeight());
    }

    /**
     * Draws the image with custom dimensions.
     *
     * @param x      the horizontal position
     * @param y      the vertical position
     * @param width  the target width
     * @param height the target height
     */
    default void draw(int x, int y, int width, int height) {
        drawWithColor(x, y, width, height, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Draws the image at its original dimensions with RGBA tinting.
     *
     * @param x the horizontal position
     * @param y the vertical position
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @param a the alpha component
     */
    default void drawWithColor(int x, int y, float r, float g, float b, float a) {
        drawWithColor(x, y, getWidth(), getHeight(), r, g, b, a);
    }

    /**
     * Draws the image with custom dimensions and RGBA tinting.
     *
     * <p>
     * This is the primary GUI rendering method that implementations
     * must provide.
     * </p>
     *
     * @param x      the horizontal position
     * @param y      the vertical position
     * @param width  the target width
     * @param height the target height
     * @param r      the red component
     * @param g      the green component
     * @param b      the blue component
     * @param a      the alpha component
     */
    void drawWithColor(int x, int y, int width, int height, float r, float g, float b, float a);

    /*
     * LEGACY GUI RENDERING
     */

    /**
     * Draws the image at its original dimensions.
     *
     * @deprecated Use {@link #draw(int, int)} instead.
     */
    @Deprecated
    default void draw(Gui gui, int x, int y) {
        draw(x, y);
    }

    /**
     * Draws the image with custom dimensions.
     *
     * @deprecated Use {@link #draw(int, int, int, int)} instead.
     */
    @Deprecated
    default void draw(Gui gui, int x, int y, int width, int height) {
        draw(x, y, width, height);
    }

    /**
     * Draws the image at its original dimensions with RGBA tinting.
     *
     * @deprecated Use
     *             {@link #drawWithColor(int, int, float, float, float, float)}
     *             instead.
     */
    @Deprecated
    default void drawWithColor(Gui gui, int x, int y, float r, float g, float b, float a) {
        drawWithColor(x, y, r, g, b, a);
    }

    /**
     * Draws the image with custom dimensions and RGBA tinting.
     *
     * @deprecated Use
     *             {@link #drawWithColor(int, int, int, int, float, float, float, float)}
     *             instead.
     */
    @Deprecated
    default void drawWithColor(Gui gui, int x, int y, int width, int height, float r, float g, float b, float a) {
        drawWithColor(x, y, width, height, r, g, b, a);
    }

    /*
     * WORLD RENDERING
     */

    /**
     * Draws the image in world space with full opacity.
     */
    default void drawWorld(TextureManager textureManager, float x1, float x2, float y1, float y2, float z) {
        drawWorldWithAlpha(textureManager, x1, x2, y1, y2, z, 1.0F);
    }

    /**
     * Draws the image in world space at Z = 0 with full opacity.
     */
    default void drawWorld(TextureManager textureManager, float x1, float x2, float y1, float y2) {
        drawWorldWithAlpha(textureManager, x1, x2, y1, y2, 0.0F, 1.0F);
    }

    /**
     * Draws the image from the origin with full opacity.
     */
    default void drawWorld(TextureManager textureManager, float x2, float y2) {
        drawWorldWithAlpha(textureManager, 0.0F, x2, 0.0F, y2, 0.0F, 1.0F);
    }

    /**
     * Draws the image in world space with custom alpha at Z = 0.
     */
    default void drawWorldWithAlpha(TextureManager textureManager, float x1, float x2, float y1, float y2,
        float alpha) {
        drawWorldWithAlpha(textureManager, x1, x2, y1, y2, 0.0F, alpha);
    }

    /**
     * Draws the image from the origin with custom alpha.
     */
    default void drawWorldWithAlpha(TextureManager textureManager, float x2, float y2, float alpha) {
        drawWorldWithAlpha(textureManager, 0.0F, x2, 0.0F, y2, 0.0F, alpha);
    }

    /**
     * Draws the image in world space with custom alpha.
     *
     * @param textureManager the texture manager
     * @param x1             the starting X position
     * @param x2             the ending X position
     * @param y1             the starting Y position
     * @param y2             the ending Y position
     * @param z              the Z position
     * @param alpha          the opacity
     */
    void drawWorldWithAlpha(TextureManager textureManager, float x1, float x2, float y1, float y2, float z,
        float alpha);

    /**
     * Returns the intrinsic image width in pixels.
     *
     * @return the width
     */
    int getWidth();

    /**
     * Returns the intrinsic image height in pixels.
     *
     * @return the height
     */
    int getHeight();
}
