package ruiseki.okcore.client.gui.component.input;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import ruiseki.okcore.client.gui.component.GuiWidget;
import ruiseki.okcore.helper.KeyBoardHelpers;
import ruiseki.okcore.helper.StringHelpers;

public class GuiTextField extends GuiWidget {

    public static final int BACKWARDS = -1;
    public static final int FORWARDS = 1;
    protected static final int CURSOR_INSERT_WIDTH = 1;
    protected static final int CURSOR_INSERT_COLOR = -3092272;
    protected static final String CURSOR_APPEND_CHARACTER = "_";
    public static final int DEFAULT_TEXT_COLOR = 14737632;
    protected static final int BORDER_COLOR_FOCUSED = -1;
    protected static final int BORDER_COLOR = -6250336;
    protected static final int BACKGROUND_COLOR = -16777216;
    protected final FontRenderer fontRenderer;
    protected String value = "";
    private int maxLength = 32;
    private int frame;
    private boolean bordered = true;
    private boolean canLoseFocus = true;
    private boolean isEditable = true;
    private boolean shiftPressed;
    private int displayPos;
    private int cursorPos;
    private int highlightPos;
    private int textColor = 14737632;
    private int textColorUneditable = 7368816;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> responder;
    @Nullable
    private String hint;

    protected GuiTextField(FontRenderer fontRenderer, int x, int y, int width, int height, String message) {
        super(x, y, width, height, message);
        this.fontRenderer = fontRenderer;
    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    public void updateScreen() {
        ++this.frame;
    }

    public void setValue(String value) {
        if (value.length() > this.maxLength) {
            this.value = value.substring(0, this.maxLength);
        } else {
            this.value = value;
        }

        this.moveCursorToEnd();
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(value);
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(i, j);
    }

    public void insertText(String value) {
        int i = Math.min(this.cursorPos, this.highlightPos);
        int j = Math.max(this.cursorPos, this.highlightPos);
        int k = this.maxLength - this.value.length() - (i - j);
        String s = ChatAllowedCharacters.filerAllowedCharacters(value);
        int l = s.length();
        if (k < l) {
            s = s.substring(0, k);
            l = k;
        }

        this.value = (new StringBuilder(this.value)).replace(i, j, s)
            .toString();
        this.setCursorPosition(i + l);
        this.setHighlightPos(this.cursorPos);
        this.onValueChange(this.value);
    }

    private void onValueChange(String value) {
        if (this.responder != null) {
            this.responder.accept(value);
        }
    }

    private void deleteText(int numWords) {
        if (GuiScreen.isCtrlKeyDown()) {
            this.deleteWords(numWords);
        } else {
            this.deleteChars(numWords);
        }
    }

    public void deleteWords(int numWords) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(numWords) - this.cursorPos);
            }
        }
    }

    public void deleteChars(int numWords) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int i = this.getCursorPos(numWords);
                int j = Math.min(i, this.cursorPos);
                int k = Math.max(i, this.cursorPos);
                if (j != k) {
                    this.value = (new StringBuilder(this.value)).delete(j, k)
                        .toString();
                    this.moveCursorTo(j);
                }
            }
        }
    }

    public int getWordPosition(int numWords) {
        return this.getWordPosition(numWords, this.getCursorPosition());
    }

    private int getWordPosition(int numWords, int startPos) {
        return this.getWordPosition(numWords, startPos, true);
    }

    private int getWordPosition(int numWords, int startPos, boolean skipWhitespace) {
        int i = startPos;
        boolean flag = numWords < 0;
        int j = Math.abs(numWords);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int length = this.value.length();
                i = this.value.indexOf(' ', i);
                if (i == -1) {
                    i = length;
                } else {
                    while (skipWhitespace && i < length && this.value.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while (skipWhitespace && i > 0 && this.value.charAt(i - 1) == ' ') {
                    --i;
                }

                while (i > 0 && this.value.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursor(int numWords) {
        this.moveCursorTo(this.getCursorPos(numWords));
    }

    private int getCursorPos(int numWords) {
        return StringHelpers.offsetByCodepoints(this.value, this.cursorPos, numWords);
    }

    public void moveCursorTo(int numWords) {
        this.setCursorPosition(numWords);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int numWords) {
        this.cursorPos = MathHelper.clamp_int(numWords, 0, this.value.length());
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else {
            this.shiftPressed = KeyBoardHelpers.isShiftKeyDown();
            if (KeyBoardHelpers.isSelectAll(keyCode)) {
                this.moveCursorToEnd();
                this.setHighlightPos(0);
                return true;
            } else if (KeyBoardHelpers.isCopy(keyCode)) {
                GuiScreen.setClipboardString(this.getHighlighted());
                return true;
            } else if (KeyBoardHelpers.isPaste(keyCode)) {
                if (this.isEditable) {
                    this.insertText(GuiScreen.getClipboardString());
                }
                return true;
            } else if (KeyBoardHelpers.isCut(keyCode)) {
                GuiScreen.setClipboardString(this.getHighlighted());
                if (this.isEditable) {
                    this.insertText("");
                }
                return true;
            } else {
                switch (keyCode) {
                    case Keyboard.KEY_BACK:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(-1);
                            this.shiftPressed = KeyBoardHelpers.isShiftKeyDown();
                        }
                        return true;

                    case Keyboard.KEY_DELETE:
                        if (this.isEditable) {
                            this.shiftPressed = false;
                            this.deleteText(1);
                            this.shiftPressed = KeyBoardHelpers.isShiftKeyDown();
                        }
                        return true;

                    case Keyboard.KEY_RIGHT:
                        if (KeyBoardHelpers.isCtrlKeyDown()) {
                            this.moveCursorTo(this.getWordPosition(1));
                        } else {
                            this.moveCursor(1);
                        }
                        return true;

                    case Keyboard.KEY_LEFT:
                        if (KeyBoardHelpers.isCtrlKeyDown()) {
                            this.moveCursorTo(this.getWordPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }
                        return true;

                    case Keyboard.KEY_HOME:
                        this.moveCursorToStart();
                        return true;

                    case Keyboard.KEY_END:
                        this.moveCursorToEnd();
                        return true;
                    default:
                        return false;
                }
            }
        }
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (ChatAllowedCharacters.isAllowedCharacter(codePoint)) {
            if (this.isEditable) {
                this.insertText(Character.toString(codePoint));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int relativeX = MathHelper.floor_double(mouseX) - this.getX();
        if (this.bordered) {
            relativeX -= 4;
        }

        String visibleText = this.fontRenderer
            .trimStringToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        this.moveCursorTo(
            this.fontRenderer.trimStringToWidth(visibleText, relativeX)
                .length() + this.displayPos);
    }

    @Override
    public void playDownSound(SoundHandler soundHandlerIn) {

    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
            if (this.isBordered()) {
                int borderColor = this.isFocused() ? -1 : -6250336;
                drawRect(
                    this.getX() - 1,
                    this.getY() - 1,
                    this.getX() + this.width + 1,
                    this.getY() + this.height + 1,
                    borderColor);
                drawRect(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
            }

            int color = this.isEditable ? this.textColor : this.textColorUneditable;
            int relativeCursorPos = this.cursorPos - this.displayPos;
            int relativeHighlightPos = this.highlightPos - this.displayPos;

            String visibleText = this.fontRenderer
                .trimStringToWidth(this.value.substring(this.displayPos), this.getInnerWidth());

            boolean isCursorVisibleInView = relativeCursorPos >= 0 && relativeCursorPos <= visibleText.length();
            boolean showBlinkingCursor = this.isFocused() && this.frame / 6 % 2 == 0 && isCursorVisibleInView;

            int startX = this.bordered ? this.getX() + 4 : this.getX();
            int startY = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
            int currentX = startX;

            if (relativeHighlightPos > visibleText.length()) {
                relativeHighlightPos = visibleText.length();
            }

            if (!visibleText.isEmpty()) {
                String textBeforeCursor = isCursorVisibleInView ? visibleText.substring(0, relativeCursorPos)
                    : visibleText;
                currentX = this.fontRenderer.drawStringWithShadow(textBeforeCursor, startX, startY, color);
            }

            boolean isCursorNotAtEnd = this.cursorPos < this.value.length()
                || this.value.length() >= this.getMaxLength();
            int cursorX = currentX;

            if (!isCursorVisibleInView) {
                cursorX = relativeCursorPos > 0 ? startX + this.width : startX;
            } else if (isCursorNotAtEnd) {
                cursorX = currentX - 1;
                --currentX;
            }

            if (!visibleText.isEmpty() && isCursorVisibleInView && relativeCursorPos < visibleText.length()) {
                this.fontRenderer
                    .drawStringWithShadow(visibleText.substring(relativeCursorPos), currentX, startY, color);
            }

            if (this.suggestion != null && visibleText.isEmpty() && !this.isFocused()) {
                this.fontRenderer.drawStringWithShadow(this.suggestion, currentX, startY, color);
            }

            if (showBlinkingCursor) {
                if (isCursorNotAtEnd) {
                    drawRect(cursorX, startY - 1, cursorX + 1, startY + 1 + 9, -3092272);
                } else {
                    this.fontRenderer.drawStringWithShadow("_", cursorX, startY, color);
                }
            }

            if (relativeHighlightPos != relativeCursorPos) {
                int highlightX = startX
                    + this.fontRenderer.getStringWidth(visibleText.substring(0, relativeHighlightPos));
                this.renderHighlight(cursorX, startY - 1, highlightX - 1, startY + 1 + 9);
            }
        }
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.value.length() > maxLength) {
            this.value = this.value.substring(0, maxLength);
            this.onValueChange(this.value);
        }
    }

    private void renderHighlight(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int temp = startX;
            startX = endX;
            endX = temp;
        }

        if (startY < endY) {
            int temp = startY;
            startY = endY;
            endY = temp;
        }

        if (endX > this.getX() + this.width) {
            endX = this.getX() + this.width;
        }

        if (startX > this.getX() + this.width) {
            startX = this.getX() + this.width;
        }

        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glLogicOp(GL11.GL_OR_REVERSE);

        tessellator.startDrawingQuads();
        tessellator.addVertex(startX, endY, 0.0D);
        tessellator.addVertex(endX, endY, 0.0D);
        tessellator.addVertex(endX, startY, 0.0D);
        tessellator.addVertex(startX, startY, 0.0D);
        tessellator.draw();

        GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    protected boolean isBordered() {
        return this.bordered;
    }

    public void setBordered(boolean bordered) {
        this.bordered = bordered;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setTextColorUneditable(int textColorUneditable) {
        this.textColorUneditable = textColorUneditable;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible && mouseX >= (double) this.getX()
            && mouseX < (double) (this.getX() + this.width)
            && mouseY >= (double) this.getY()
            && mouseY < (double) (this.getY() + this.height);
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.canLoseFocus || focused) {
            super.setFocused(focused);
            if (focused) {
                this.frame = 0;
            }
        }
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int index) {
        int i = this.value.length();
        this.highlightPos = MathHelper.clamp_int(index, 0, i);
        if (this.fontRenderer != null) {
            if (this.displayPos > i) {
                this.displayPos = i;
            }

            int j = this.getInnerWidth();
            String s = this.fontRenderer.trimStringToWidth(this.value.substring(this.displayPos), j);
            int k = s.length() + this.displayPos;
            if (this.highlightPos == this.displayPos) {
                this.displayPos -= this.fontRenderer.trimStringToWidth(this.value, j, true)
                    .length();
            }

            if (this.highlightPos > k) {
                this.displayPos += this.highlightPos - k;
            } else if (this.highlightPos <= this.displayPos) {
                this.displayPos -= this.displayPos - this.highlightPos;
            }

            this.displayPos = MathHelper.clamp_int(this.displayPos, 0, i);
        }

    }

    public void setCanLoseFocus(boolean canLoseFocus) {
        this.canLoseFocus = canLoseFocus;
    }

    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }

    public int getScreenX(int index) {
        return index > this.value.length() ? this.getX()
            : this.getX() + this.fontRenderer.getStringWidth(this.value.substring(0, index));
    }

    public void setHint(@Nullable String hint) {
        this.hint = hint;
    }
}
