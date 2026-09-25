package ruiseki.okcore.client.gui.component.input;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.client.gui.IGuiEventListener;
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
    implements IWidgetEventListener, IWidgetRenderable, IGuiEventListener {

    private final boolean background;
    private IInputListener listener;
    private final String narrationMessage;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> responder;
    @Nullable
    private String hint;

    public GuiTextFieldExtended(FontRenderer fontrenderer, int x, int y, int width, int height, String narrationMessage,
        boolean background) {
        super(fontrenderer, x, y, width, height);
        this.background = background;
        this.narrationMessage = narrationMessage;
    }

    public GuiTextFieldExtended(FontRenderer fontrenderer, int x, int y, int width, int height,
        String narrationMessage) {
        this(fontrenderer, x, y, width, height, narrationMessage, false);
    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    public void setListener(IInputListener listener) {
        this.listener = listener;
    }

    public int getInnerWidth() {
        return this.width - 7;
    }

    @Override
    public void writeText(String string) {
        super.writeText(string);
        this.onValueChange(string);
    }

    @Override
    public void moveCursorBy(int p_146182_1_) {
        super.moveCursorBy(p_146182_1_);
        this.onValueChange(this.text);
    }

    @Override
    public void setMaxStringLength(int length) {
        super.setMaxStringLength(length);
        if (this.text.length() > length) {
            this.onValueChange(this.text);
        }
    }

    private void onValueChange(String p_94175_) {
        if (this.responder != null) {
            this.responder.accept(p_94175_);
        }
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
        } else {
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
            } else {
                switch (keyCode) {
                    case 14: // Backspace
                        if (this.isEnabled) {
                            this.deleteFromCursor(-1);
                            this.notifyListenerIfChanged(oldText);
                        }
                        return true;
                    case 199: // Home
                        this.setCursorPositionZero();
                        return true;
                    case 203: // Left Arrow
                        if (KeyBoardHelpers.isCtrlKeyDown()) {
                            this.setCursorPosition(this.getNthWordFromCursor(-1));
                        } else {
                            this.moveCursorBy(-1);
                        }
                        return true;
                    case 205: // Right Arrow
                        if (KeyBoardHelpers.isCtrlKeyDown()) {
                            this.setCursorPosition(this.getNthWordFromCursor(1));
                        } else {
                            this.moveCursorBy(1);
                        }
                        return true;
                    case 207: // End
                        this.setCursorPositionEnd();
                        return true;
                    case 211: // Delete
                        if (this.isEnabled) {
                            this.deleteFromCursor(1);
                            this.notifyListenerIfChanged(oldText);
                        }
                        return true;
                    default:
                        return false;
                }
            }
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

    @Override
    public int getWidth() {
        return super.getWidth();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String getTooltip() {
        return narrationMessage;
    }

    public boolean isEnable() {
        return isEnabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public FontRenderer getFontRenderer() {
        return field_146211_a;
    }

    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public final void drawTextBox() {
        if (this.isVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                drawRect(
                    this.xPosition - 1,
                    this.yPosition - 1,
                    this.xPosition + this.width + 1,
                    this.yPosition + this.height + 1,
                    -6250336);
                drawRect(
                    this.xPosition,
                    this.yPosition,
                    this.xPosition + this.width,
                    this.yPosition + this.height,
                    -16777216);
            }

            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.field_146211_a
                .trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int i1 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int j1 = l;

            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.field_146211_a.drawStringWithShadow(s1, l, i1, i);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.field_146211_a.drawStringWithShadow(s.substring(j), j1, i1, i);
            }

            if (this.hint != null && s.isEmpty() && !this.isFocused()) {
                this.field_146211_a.drawStringWithShadow(this.hint, j1, i1, i);
            }

            if (!flag2 && this.suggestion != null) {
                this.field_146211_a.drawStringWithShadow(this.suggestion, k1 - 1, i1, -8355712);
            }

            if (flag1) {
                if (flag2) {
                    Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.field_146211_a.FONT_HEIGHT, -3092272);
                } else {
                    this.field_146211_a.drawStringWithShadow("_", k1, i1, i);
                }
            }

            if (k != j) {
                int l1 = l + this.field_146211_a.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.field_146211_a.FONT_HEIGHT);
            }

        }
    }

    @Override
    @Deprecated
    public final void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        // Unused legacy method
    }

    @Override
    @Deprecated
    public final boolean textboxKeyTyped(char typedChar, int keyCode) {
        return false;
    }

    public void playDownSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
    }

}
