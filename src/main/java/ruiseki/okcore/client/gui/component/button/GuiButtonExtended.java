package ruiseki.okcore.client.gui.component.button;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * An extended {@link GuiButton} that is better resizable.
 * 
 * @author rubensworks
 *
 */
@SideOnly(Side.CLIENT)
public abstract class GuiButtonExtended extends GuiButton {

    private final boolean background;

    public GuiButtonExtended(int x, int y, int width, int height, String string, GuiButton.OnPress onPress,
        boolean background) {
        super(x, y, width, height, string, onPress);
        this.background = background;
    }

    @Override
    public void onPress() {
        if (this.isActive()) {
            super.onPress();
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

    protected void drawBackground() {
        RenderHelpers.bindTexture(WIDGETS_LOCATION);
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
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            if (this.background) {
                this.drawBackground();
            }
            this.drawButtonInner(mouseX, mouseY, this.isHovered);
        }
    }

    protected abstract void drawButtonInner(int mouseX, int mouseY, boolean mouseOver);
}
