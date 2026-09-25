package ruiseki.okcore.client.gui.component;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.client.gui.IGuiEventListener;

public abstract class GuiWidget extends Gui implements IGuiEventListener, IWidgetEventListener, IWidgetRenderable {

    private int x;
    private int y;
    protected final int width;
    protected final int height;
    private final String narrationMessage;
    protected boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    private boolean focused;

    protected GuiWidget(int x, int y, int width, int height, String narrationMessage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.narrationMessage = narrationMessage;
    }

    public void playDownSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.active && this.visible
            && mouseX >= (double) this.getX()
            && mouseY >= (double) this.getY()
            && mouseX < (double) (this.getX() + this.width)
            && mouseY < (double) (this.getY() + this.height);
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public String getTooltip() {
        return narrationMessage;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            this.isHovered = mouseX >= this.getX() && mouseY >= this.getY()
                && mouseX < this.getX() + this.width
                && mouseY < this.getY() + this.height;
            drawWidget(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.isMouseOver(mouseX, mouseY);
                if (flag) {
                    this.playDownSound(
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
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.isValidClickButton(button)) {
            this.onDrag(mouseX, mouseY, dragX, dragY);
            return true;
        }
        return false;
    }

    protected boolean isValidClickButton(int button) {
        return button == 0;
    }
}
