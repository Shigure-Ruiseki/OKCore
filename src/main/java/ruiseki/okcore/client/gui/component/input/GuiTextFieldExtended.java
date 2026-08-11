package ruiseki.okcore.client.gui.component.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;

/**
 * An extended text field.
 *
 * @author rubensworks
 */
public class GuiTextFieldExtended extends GuiTextField {

    private final boolean background;
    private IInputListener listener;

    public GuiTextFieldExtended(int componentId, FontRenderer fontrenderer, int x, int y, int width, int height,
        boolean background) {
        super(fontrenderer, x, y, width, height);
        this.background = background;
    }

    public GuiTextFieldExtended(int componentId, FontRenderer fontrenderer, int x, int y, int width, int height) {
        this(componentId, fontrenderer, x, y, width, height, false);
    }

    public void setListener(IInputListener listener) {
        this.listener = listener;
    }

    protected void drawBackground(Minecraft minecraft, int mouseX, int mouseY) {
        minecraft.renderEngine.bindTexture(Images.WIDGETS);
        GlStateManager.color(1, 1, 1, 1);

        xPosition--;
        yPosition--;
        drawTexturedModalRect(xPosition, yPosition, 0, 0, width / 2, height / 2);// top left
        drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 0, width / 2, height / 2);// top right
        drawTexturedModalRect(xPosition, yPosition + height / 2, 0, 20 - height / 2, width / 2, height / 2);// bottom
                                                                                                            // left
        drawTexturedModalRect(
            xPosition + width / 2,
            yPosition + height / 2,
            200 - width / 2,
            20 - height / 2,
            width / 2,
            height / 2);// bottom right
        xPosition++;
        yPosition++;
    }

    @Override
    public void setText(String value) {
        super.setText(value);
        if (listener != null) listener.onChanged();
    }

    /**
     * Draw the text box with mouse sensitive parameters.
     *
     * @param minecraft Minecraft instance.
     * @param mouseX    Mouse X
     * @param mouseY    Mouse Y
     */
    public void drawTextBox(Minecraft minecraft, int mouseX, int mouseY) {
        if (background) {
            drawBackground(minecraft, mouseX, mouseY);
        }
        super.drawTextBox();
    }

    @Deprecated
    public void drawTextBox() {
        super.drawTextBox();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && mouseX >= this.xPosition
            && mouseX < this.xPosition + this.width
            && mouseY >= this.yPosition
            && mouseY < this.yPosition + this.height) {
            // Select everything
            this.setFocused(true);
            this.setCursorPosition(0);
            this.setSelectionPos(Integer.MAX_VALUE);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
