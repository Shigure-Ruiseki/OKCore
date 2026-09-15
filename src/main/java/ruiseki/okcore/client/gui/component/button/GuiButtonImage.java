package ruiseki.okcore.client.gui.component.button;

import net.minecraft.client.Minecraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.image.IImage;

/**
 * A button with an image.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class GuiButtonImage extends GuiButtonExtended {

    private IImage[] images;
    private final int offsetX, offsetY;

    /**
     * Make a new instance.
     *
     * @param id         The ID.
     * @param x          X
     * @param y          Y
     * @param width      Width
     * @param height     Height
     * @param images     The images to render. First images are rendered behind later images.
     * @param offsetX    The x coordinate for the image inside the button.
     * @param offsetY    The y coordinate for the image inside the button.
     * @param onPress    The click handler.
     * @param background If the button background should be rendered.
     */
    public GuiButtonImage(int id, int x, int y, int width, int height, IImage[] images, int offsetX, int offsetY,
        OnPress onPress, boolean background) {
        super(id, x, y, width, height, "", onPress, background);
        this.images = images != null ? images : new IImage[0];
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    /**
     * Make a new instance.
     *
     * @param id      The ID.
     * @param x       X
     * @param y       Y
     * @param images  The images to render
     * @param onPress The click handler.
     */
    public GuiButtonImage(int id, int x, int y, IImage[] images, OnPress onPress) {
        this(
            id,
            x,
            y,
            (images != null && images.length > 0 && images[0] != null) ? images[0].getWidth() : 0,
            (images != null && images.length > 0 && images[0] != null) ? images[0].getHeight() : 0,
            images,
            0,
            0,
            onPress,
            false);
    }

    /**
     * Make a new instance.
     *
     * @param id         The ID.
     * @param x          X
     * @param y          Y
     * @param width      Width
     * @param height     Height
     * @param image      The image to render
     * @param offsetX    The x coordinate for the image inside the button.
     * @param offsetY    The y coordinate for the image inside the button.
     * @param onPress    The click handler.
     * @param background If the button background should be rendered.
     */
    public GuiButtonImage(int id, int x, int y, int width, int height, IImage image, int offsetX, int offsetY,
        OnPress onPress, boolean background) {
        this(id, x, y, width, height, new IImage[] { image }, offsetX, offsetY, onPress, background);
    }

    /**
     * Make a new instance.
     *
     * @param id      The ID.
     * @param x       X
     * @param y       Y
     * @param image   The image to render
     * @param onPress The click handler.
     */
    public GuiButtonImage(int id, int x, int y, IImage image, OnPress onPress) {
        this(
            id,
            x,
            y,
            image != null ? image.getWidth() : 0,
            image != null ? image.getHeight() : 0,
            image,
            0,
            0,
            onPress,
            false);
    }

    @Override
    protected void drawButtonInner(Minecraft minecraft, int mouseX, int mouseY, boolean mouseOver) {
        if (this.images != null) {
            for (IImage image : this.images) {
                if (image != null) {
                    image.draw(this, this.xPosition + this.offsetX, this.yPosition + this.offsetY);
                }
            }
        }
    }

    public void setImage(IImage image) {
        if (this.images == null || this.images.length == 0) {
            this.images = new IImage[] { image };
        } else {
            this.images[0] = image;
        }
    }

    public void setImages(IImage[] images) {
        this.images = images != null ? images : new IImage[0];
    }

    public IImage[] getImages() {
        return this.images;
    }
}
