package ruiseki.okcore.client.gui.component.button;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.component.GuiWidget;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.RenderHelpers;

@SideOnly(Side.CLIENT)
public abstract class GuiButton extends GuiWidget {

    protected static final int TEXTURE_Y_OFFSET = 46;
    protected static final int TEXTURE_WIDTH = 200;
    protected static final int TEXTURE_HEIGHT = 20;
    protected static final int TEXTURE_BORDER_X = 20;
    protected static final int TEXTURE_BORDER_Y = 4;
    protected static final int TEXT_MARGIN = 2;

    protected final OnPress onPress;

    public GuiButton(int x, int y, int width, int height, String string, OnPress onPress) {
        super(x, y, width, height, string);
        this.onPress = onPress;
    }

    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        RenderHelpers.bindTexture(WIDGETS_LOCATION);
        GlStateManager.color(1.0F, 1.0F, 1.0F, this.alpha);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableDepth();
        this.drawTexturedModalRect(this.getX(), this.getY(), 0, this.getTextureY(), this.width / 2, this.height);
        this.drawTexturedModalRect(
            this.getX() + this.width / 2,
            this.getY(),
            200 - this.width / 2,
            this.getTextureY(),
            this.width / 2,
            this.height);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    protected int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHovered()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    @SideOnly(Side.CLIENT)
    public interface OnPress {

        void onPress(GuiButton button);
    }
}
