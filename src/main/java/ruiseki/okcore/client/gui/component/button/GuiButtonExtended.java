package ruiseki.okcore.client.gui.component.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.renderer.GlStateManager;

/**
 * An extended {@link net.minecraft.client.gui.GuiButton} which is better resizable.
 * Based on chickenbones' GuiNEIButton.
 *
 * @author rubensworks
 *
 */
public abstract class GuiButtonExtended extends GuiButton {

    private final boolean background;
    protected final OnPress onPress;

    /**
     * Make a new instance.
     *
     * @param id         The ID.
     * @param x          X
     * @param y          Y
     * @param width      Width
     * @param height     Height
     * @param string     The string to print.
     * @param onPress    The click action handler.
     * @param background If the background of the button should be rendered.
     */
    public GuiButtonExtended(int id, int x, int y, int width, int height, String string, OnPress onPress,
        boolean background) {
        super(id, x, y, width, height, string);
        this.background = background;
        this.onPress = onPress;
    }

    protected void drawBackground(Minecraft minecraft, int hoverState) {
        minecraft.renderEngine.bindTexture(buttonTextures);
        GlStateManager.color(1, 1, 1, 1);

        int halfW = this.width / 2;
        int halfH = this.height / 2;

        // Top Left
        drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + hoverState * 20, halfW, halfH);
        // Top Right
        drawTexturedModalRect(this.xPosition + halfW, this.yPosition, 200 - halfW, 46 + hoverState * 20, halfW, halfH);
        // Bottom Left
        drawTexturedModalRect(
            this.xPosition,
            this.yPosition + halfH,
            0,
            46 + hoverState * 20 + 20 - halfH,
            halfW,
            halfH);
        // Bottom Right
        drawTexturedModalRect(
            this.xPosition + halfW,
            this.yPosition + halfH,
            200 - halfW,
            46 + hoverState * 20 + 20 - halfH,
            halfW,
            halfH);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (this.visible) {
            this.field_146123_n = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;

            int hoverState = this.getHoverState(this.field_146123_n);

            if (this.background) {
                this.drawBackground(minecraft, hoverState);
            }

            this.drawButtonInner(minecraft, mouseX, mouseY, this.field_146123_n);
            this.mouseDragged(minecraft, mouseX, mouseY);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        if (super.mousePressed(minecraft, mouseX, mouseY)) {
            this.func_146113_a(minecraft.getSoundHandler());

            // Triggers callback
            this.onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    public void onClick(int mouseX, int mouseY) {
        if (this.onPress != null) {
            this.onPress.onPress(this);
        }
    }

    protected abstract void drawButtonInner(Minecraft minecraft, int mouseX, int mouseY, boolean mouseOver);

    public boolean isHovered() {
        return this.field_146123_n;
    }

    public boolean hasBackground() {
        return this.background;
    }

    @SideOnly(Side.CLIENT)
    public interface OnPress {

        void onPress(GuiButtonExtended button);
    }
}
