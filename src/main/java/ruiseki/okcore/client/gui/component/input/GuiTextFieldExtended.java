package ruiseki.okcore.client.gui.component.input;

import net.minecraft.client.gui.FontRenderer;

import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * An extended text field.
 * 
 * @author rubensworks
 */
public class GuiTextFieldExtended extends GuiTextField {

    private final boolean background;
    private IInputListener listener;

    public GuiTextFieldExtended(FontRenderer fontrenderer, int x, int y, int width, int height, String narrationMessage,
        boolean background) {
        super(fontrenderer, x, y, width, height, narrationMessage);
        this.background = background;
    }

    public GuiTextFieldExtended(FontRenderer fontrenderer, int x, int y, int width, int height,
        String narrationMessage) {
        this(fontrenderer, x, y, width, height, narrationMessage, false);
    }

    public void setListener(IInputListener listener) {
        this.listener = listener;
    }

    @Override
    public int getInnerWidth() {
        return this.width - 7;
    }

    protected void drawBackground(int mouseX, int mouseY, float partialTicks) {
        RenderHelpers.bindTexture(Images.WIDGETS);
        GlStateManager.color(1, 1, 1, 1);

        setX(getX() - 1);
        setY(getY() - 1);
        // top left
        drawTexturedModalRect(getX(), getY(), 0, 0, width / 2, height / 2);
        // top right
        drawTexturedModalRect(getX() + width / 2, getY(), 200 - width / 2, 0, width / 2, height / 2);
        // bottom left
        drawTexturedModalRect(getX(), getY() + height / 2, 0, 20 - height / 2, width / 2, height / 2);
        // bottom right
        drawTexturedModalRect(
            getX() + width / 2,
            getY() + height / 2,
            200 - width / 2,
            20 - height / 2,
            width / 2,
            height / 2);
        setX(getX() + 1);
        setY(getY() + 1);
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        if (listener != null) listener.onChanged();
    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (background) {
            drawBackground(mouseX, mouseY, partialTicks);
        }
        super.drawWidget(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 1 && mouseX >= this.getX()
            && mouseX < this.getX() + this.width
            && mouseY >= this.getY()
            && mouseY < this.getY() + this.height) {
            // Select everything
            this.setFocused(true);
            this.moveCursorTo(0);
            this.setHighlightPos(Integer.MAX_VALUE);
            return true;
        }
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            this.setFocused(true);
            return true;
        } else {
            this.setFocused(false);
        }
        return false;
    }
}
