package ruiseki.okcore.client.gui.component.button;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.client.gui.component.GuiWidget;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * An extended {@link net.minecraft.client.gui.GuiButton} which is better resizable
 * and implements {@link IGuiEventListener} for enhanced event handling.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public abstract class GuiButtonExtended extends GuiWidget {

    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");

    private final boolean background;
    protected final OnPress onPress;

    /**
     * @param x          X position
     * @param y          Y position
     * @param width      Width of the button
     * @param height     Height of the button
     * @param string     The string to print
     * @param onPress    The click action handler
     * @param background If the background of the button should be rendered
     */
    public GuiButtonExtended(int x, int y, int width, int height, String string, OnPress onPress, boolean background) {
        super(x, y, width, height, string);
        this.background = background;
        this.onPress = onPress;
    }

    protected void drawBackground() {
        RenderHelpers.bindTexture(buttonTextures);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        int textureY = getTextureY();
        drawTexturedModalRect(getX(), getY(), 0, textureY, width / 2, height / 2);// Top Left
        drawTexturedModalRect(getX() + width / 2, getY(), 200 - width / 2, textureY, width / 2, height / 2);// Top Right
        drawTexturedModalRect(getX(), getY() + height / 2, 0, textureY + 20 - height / 2, width / 2, height / 2);// Bottom
                                                                                                                 // Left
        drawTexturedModalRect(
            getX() + width / 2,
            getY() + height / 2,
            200 - width / 2,
            textureY + 20 - height / 2,
            width / 2,
            height / 2);// Bottom Right
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY()
                && mouseX < this.getX() + this.width
                && mouseY < this.getY() + this.height;
            this.drawWidget(mouseX, mouseY, partialTicks);

        }
    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            GlStateManager.enableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (this.background) {
                this.drawBackground();
            }
            this.drawButtonInner(mouseX, mouseY, this.isHovered);
        }
    }

    protected abstract void drawButtonInner(int mouseX, int mouseY, boolean mouseOver);

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    public boolean hasBackground() {
        return this.background;
    }

    public void onPress() {
        if (this.active) {
            this.onPress.onPress(this);
        }
    }

    protected int getYImage() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return i;
    }

    protected int getTextureY() { // Copy from AbstractButton
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered() || this.isFocused();
    }

    @SideOnly(Side.CLIENT)
    public interface OnPress {

        void onPress(GuiButtonExtended button);
    }
}
