package ruiseki.okcore.client.gui.component.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;

import ruiseki.okcore.client.gui.IGuiEventListener;
import ruiseki.okcore.client.gui.IRenderable;
import ruiseki.okcore.client.gui.component.IWidgetEventListener;
import ruiseki.okcore.client.gui.component.IWidgetRenderable;
import ruiseki.okcore.client.gui.image.Images;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.KeyBoardHelpers;
import ruiseki.okcore.helper.RenderHelpers;

/**
 * An extended text field supporting modern event listeners and widget rendering.
 *
 * @author rubensworks
 */
public class GuiTextFieldExtended extends GuiTextField
    implements IWidgetEventListener, IWidgetRenderable, IGuiEventListener, IRenderable {

    private final boolean background;
    private IInputListener listener;

    public GuiTextFieldExtended(FontRenderer fontrenderer, int x, int y, int width, int height, boolean background) {
        super(fontrenderer, x, y, width, height);
        this.background = background;
    }

    public GuiTextFieldExtended(FontRenderer fontrenderer, int x, int y, int width, int height) {
        this(fontrenderer, x, y, width, height, false);
    }

    public void setListener(IInputListener listener) {
        this.listener = listener;
    }

    public int getInnerWidth() {
        return this.width - 7;
    }

    protected void drawBackground(int mouseX, int mouseY, float partialTicks) {
        RenderHelpers.bindTexture(Images.WIDGETS);
        GlStateManager.color(1, 1, 1, 1);

        xPosition--;
        yPosition--;
        drawTexturedModalRect(xPosition, yPosition, 0, 0, width / 2, height / 2); // top left
        drawTexturedModalRect(xPosition + width / 2, yPosition, 200 - width / 2, 0, width / 2, height / 2); // top right
        drawTexturedModalRect(xPosition, yPosition + height / 2, 0, 20 - height / 2, width / 2, height / 2); // bottom
                                                                                                             // left
        drawTexturedModalRect(
            xPosition + width / 2,
            yPosition + height / 2,
            200 - width / 2,
            20 - height / 2,
            width / 2,
            height / 2); // bottom right
        xPosition++;
        yPosition++;
    }

    @Override
    public void setText(String value) {
        super.setText(value);
        if (this.listener != null) {
            this.listener.onChanged();
        }
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        String oldText = this.getText();
        boolean result = super.textboxKeyTyped(typedChar, keyCode);
        if (result && !this.getText()
            .equals(oldText) && this.listener != null) {
            this.listener.onChanged();
        }
        return result;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.getVisible()) {
            this.drawWidget(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (this.background) {
            this.drawBackground(mouseX, mouseY, partialTicks);
        }
        this.drawTextBox();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (!this.getVisible()) {
            return false;
        }

        boolean hovered = isMouseOver(mouseX, mouseY);

        if (mouseButton == 1 && hovered) {
            this.setFocused(true);
            this.setCursorPositionZero();
            this.setSelectionPos(
                this.getText()
                    .length());
            return true;
        }

        if (mouseButton == 0 && hovered) {
            this.setFocused(true);
            this.onClick(mouseX, mouseY);
            return true;
        }

        this.setFocused(false);
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int relativeX = MathHelper.floor_double(mouseX) - this.getX();
        if (this.getEnableBackgroundDrawing()) {
            relativeX -= 4;
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        String trimmedText = fontRenderer.trimStringToWidth(
            this.getText()
                .substring(this.lineScrollOffset),
            this.getInnerWidth());
        this.setCursorPosition(
            fontRenderer.trimStringToWidth(trimmedText, relativeX)
                .length() + this.lineScrollOffset);
    }

    public boolean canConsumeInput() {
        return this.getVisible() && this.isFocused() && this.isEnabled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.canConsumeInput()) {
            return false;
        }

        String oldText = this.getText();

        if (KeyBoardHelpers.isSelectAll(keyCode)) {
            this.setCursorPositionZero();
            this.setSelectionPos(
                this.getText()
                    .length());
            return true;
        } else if (KeyBoardHelpers.isCopy(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        } else if (KeyBoardHelpers.isPaste(keyCode)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString());
                this.notifyListenerIfChanged(oldText);
            }
            return true;
        } else if (KeyBoardHelpers.isCut(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
                this.writeText("");
                this.notifyListenerIfChanged(oldText);
            }
            return true;
        }

        switch (keyCode) {
            case Keyboard.KEY_BACK: // Backspace (14)
                if (this.isEnabled) {
                    this.deleteWords(-1);
                    this.notifyListenerIfChanged(oldText);
                }
                return true;

            case Keyboard.KEY_DELETE: // Delete (211)
                if (this.isEnabled) {
                    this.deleteWords(1);
                    this.notifyListenerIfChanged(oldText);
                }
                return true;

            case Keyboard.KEY_RIGHT: // Arrow right (205)
                if (KeyBoardHelpers.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    this.moveCursorBy(1);
                }
                return true;

            case Keyboard.KEY_LEFT: // Arrow left (203)
                if (KeyBoardHelpers.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    this.moveCursorBy(-1);
                }
                return true;

            case Keyboard.KEY_HOME: // Home (199)
                this.setCursorPositionZero();
                return true;

            case Keyboard.KEY_END: // End (207)
                this.setCursorPositionEnd();
                return true;

            default:
                return false;
        }
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.canConsumeInput()) {
            return false;
        }

        if (ChatAllowedCharacters.isAllowedCharacter(codePoint)) {
            if (this.isEnabled) {
                String oldText = this.getText();
                this.writeText(Character.toString(codePoint));
                this.notifyListenerIfChanged(oldText);
            }
            return true;
        }

        return false;
    }

    private void notifyListenerIfChanged(String oldText) {
        if (this.listener != null && !this.getText()
            .equals(oldText)) {
            this.listener.onChanged();
        }
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.xPosition && mouseX < this.xPosition + this.width
            && mouseY >= this.yPosition
            && mouseY < this.yPosition + this.height;
    }

    @Override
    public int getX() {
        return this.xPosition;
    }

    @Override
    public int getY() {
        return this.yPosition;
    }

    @Override
    public void setX(int x) {
        this.xPosition = x;
    }

    @Override
    public void setY(int y) {
        this.yPosition = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    @Deprecated
    public final void drawTextBox() {
        super.drawTextBox();
    }

    @Override
    @Deprecated
    public final void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Unused legacy method
    }
}
