package ruiseki.okcore.client.gui.image;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * Interface for graphics objects that can be rendered.
 *
 * @author rubensworks
 */
public interface IImage {

    /**
     * Draw this image.
     *
     * @param gui The gui helper object.
     * @param x   The x position.
     * @param y   The y position.
     */
    public void draw(Gui gui, int x, int y);

    /**
     * Draw this image with color tinting.
     *
     * @param gui The gui helper object.
     * @param x   The x position.
     * @param y   The y position.
     * @param r   Red
     * @param g   Green
     * @param b   Blue
     * @param a   Alpha
     */
    public void drawWithColor(Gui gui, int x, int y, float r, float g, float b, float a);

    /**
     * Draw the image in the world.
     *
     * @param textureManager The texture manager.
     * @param x1             Start X
     * @param x2             End X
     * @param y1             Start Y
     * @param y2             End Y
     * @param z              Z
     */
    default void drawWorld(TextureManager textureManager, float x1, float x2, float y1, float y2, float z) {
        drawWorldWithAlpha(textureManager, x1, x2, y1, y2, z, 1.0F);
    }

    /**
     * Draw the image in the world.
     *
     * @param textureManager The texture manager.
     * @param x1             Start X
     * @param x2             End X
     * @param y1             Start Y
     * @param y2             End Y
     */
    default void drawWorld(TextureManager textureManager, float x1, float x2, float y1, float y2) {
        drawWorldWithAlpha(textureManager, x1, x2, y1, y2, 0.0F, 1.0F);
    }

    /**
     * Draw the image in the world.
     *
     * @param textureManager The texture manager.
     * @param x2             End X
     * @param y2             End Y
     */
    default void drawWorld(TextureManager textureManager, float x2, float y2) {
        drawWorldWithAlpha(textureManager, 0.0F, x2, 0.0F, y2, 0.0F, 1.0F);
    }

    /**
     * Draw the image in the world.
     *
     * @param textureManager The texture manager.
     * @param x1             Start X
     * @param x2             End X
     * @param y1             Start Y
     * @param y2             End Y
     * @param alpha          The alpha to render with
     */
    default void drawWorldWithAlpha(TextureManager textureManager, float x1, float x2, float y1, float y2,
        float alpha) {
        drawWorldWithAlpha(textureManager, x1, x2, y1, y2, 0.0F, alpha);
    }

    /**
     * Draw the image in the world.
     *
     * @param textureManager The texture manager.
     * @param x2             End X
     * @param y2             End Y
     * @param alpha          The alpha to render with
     */
    default void drawWorldWithAlpha(TextureManager textureManager, float x2, float y2, float alpha) {
        drawWorldWithAlpha(textureManager, 0.0F, x2, 0.0F, y2, 0.0F, alpha);
    }

    /**
     * Draw the image in the world.
     *
     * @param textureManager The texture manager.
     * @param x1             Start X
     * @param x2             End X
     * @param y1             Start Y
     * @param y2             End Y
     * @param z              Z
     * @param alpha          The alpha to render with
     */
    void drawWorldWithAlpha(TextureManager textureManager, float x1, float x2, float y1, float y2, float z,
        float alpha);

    /**
     * @return The width in pixels.
     */
    public int getWidth();

    /**
     * @return The height in pixels.
     */
    public int getHeight();

}
