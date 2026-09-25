package ruiseki.okcore.client.gui.component.button;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.client.gui.component.IWidgetEventListener;
import ruiseki.okcore.client.gui.component.IWidgetRenderable;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * An extended {@link net.minecraft.client.gui.GuiButton} which is better resizable
 * and implements {@link IGuiEventListener} for enhanced event handling.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public abstract class GuiButtonExtended extends GuiButton
    implements IWidgetEventListener, IGuiEventListener, IWidgetRenderable {

    private final boolean background;
    protected final OnPress onPress;
    private boolean focused;

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
        super(0, x, y, width, height, string);
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
            this.field_146123_n = mouseX >= this.getX() && mouseY >= this.getY()
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
            this.drawButtonInner(mouseX, mouseY, this.field_146123_n);
        }
    }

    protected abstract void drawButtonInner(int mouseX, int mouseY, boolean mouseOver);

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
    }

    public boolean isHovered() {
        return this.field_146123_n;
    }

    public boolean hasBackground() {
        return this.background;
    }

    public void onPress() {
        if (this.enabled) {
            this.onPress.onPress(this);
        }
    }

    protected int getYImage() {
        int i = 1;
        if (!this.enabled) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return i;
    }

    protected int getTextureY() { // Copy from AbstractButton
        int i = 1;
        if (!this.enabled) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }

        return 46 + i * 20;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.enabled && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.isMouseOver(mouseX, mouseY);
                if (flag) {
                    this.func_146113_a(
                        Minecraft.getMinecraft()
                            .getSoundHandler());
                    this.onClick(mouseX, mouseY);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.isValidClickButton(button)) {
            this.onRelease(mouseX, mouseY);
            return true;
        } else {
            return false;
        }
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isValidClickButton(button)) {
            this.onDrag(mouseX, mouseY, dragX, dragY);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= this.xPosition
            && mouseY >= this.yPosition
            && mouseX < this.xPosition + this.width
            && mouseY < this.yPosition + this.height;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    public boolean isHoveredOrFocused() {
        return this.isHovered() || this.isFocused();
    }

    @Override
    public int getX() {
        return xPosition;
    }

    @Override
    public int getY() {
        return yPosition;
    }

    @Override
    public void setX(int x) {
        xPosition = x;
    }

    @Override
    public void setY(int y) {
        yPosition = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getTooltip() {
        return displayString;
    }

    @SideOnly(Side.CLIENT)
    public interface OnPress {

        void onPress(GuiButtonExtended button);
    }

    @Override
    @Deprecated
    public final void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        drawScreen(mouseX, mouseY, 0);
    }

    @Override
    @Deprecated
    protected final void mouseDragged(Minecraft mc, int mouseX, int mouseY) {

    }

    @Override
    @Deprecated
    public final void mouseReleased(int mouseX, int mouseY) {

    }

    @Override
    @Deprecated
    public final boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return false;
    }
}
