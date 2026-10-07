package ruiseki.okcore.client.gui.component.input;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

import org.lwjgl.input.Keyboard;

import ruiseki.okcore.client.gui.component.GuiWidget;
import ruiseki.okcore.helper.KeyBoardHelpers;
import ruiseki.okcore.helper.StringHelpers;

public class GuiTextField extends GuiWidget {

    // 1. CONSTANTS - Rendering & Cursor Configuration
    public static final int BACKWARDS = -1;
    public static final int FORWARDS = 1;
    public static final int DEFAULT_TEXT_COLOR = 14737632;
    private static final int CURSOR_INSERT_WIDTH = 1;
    private static final int CURSOR_INSERT_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private static final int BORDER_COLOR_FOCUSED = -1;
    private static final int BORDER_COLOR = -6250336;
    private static final int BACKGROUND_COLOR = -16777216;

    // 2. CORE COMPONENTS & UTILITIES
    private final FontRenderer fontRenderer;

    // 3. TEXT CONTENT & BOUNDS
    private String value = "";
    private int maxLength = 32;

    // 4. CURSOR & SELECTION POINTERS
    private int cursorPos;
    private int highlightPos;
    private int displayPos;

    // 5. STATES & FLAGS
    private boolean bordered = true;
    private boolean canLoseFocus = true;
    private boolean isEditable = true;
    private boolean shiftPressed;
    private int frame;

    // 6. COLOR CONFIGURATIONS
    private int textColor = DEFAULT_TEXT_COLOR;
    private int textColorUneditable = 7368816;

    // 7. INPUT LISTENERS & FILTERS
    @Nullable
    private Consumer<String> responder;
    private Predicate<String> filter = Objects::nonNull;

    // 8. HINTS & SUGGESTIONS
    @Nullable
    private String suggestion;
    @Nullable
    private String hint;

    // 9. CONSTRUCTORS
    protected GuiTextField(FontRenderer fontRenderer, int x, int y, int width, int height, String message) {
        this(fontRenderer, x, y, width, height, null, message);
    }

    protected GuiTextField(FontRenderer fontRenderer, int x, int y, int width, int height,
        @Nullable GuiTextField textField, String message) {
        super(x, y, width, height, message);
        this.fontRenderer = fontRenderer;
        if (textField != null) {

        }
    }

    public void setResponder(Consumer<String> responder) {
        this.responder = responder;
    }

    public void tick() {
        ++this.frame;
    }

    public void setValue(String newValue) {
        if (this.filter.test(newValue)) {
            if (newValue.length() > this.maxLength) {
                this.value = newValue.substring(0, this.maxLength);
            } else {
                this.value = newValue;
            }

            this.moveCursorToEnd();
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(newValue);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int start = Math.min(this.cursorPos, this.highlightPos);
        int end = Math.max(this.cursorPos, this.highlightPos);
        return this.value.substring(start, end);
    }

    public void setFilter(Predicate<String> filter) {
        this.filter = filter;
    }

    public void insertText(String input) {
        int start = Math.min(this.cursorPos, this.highlightPos);
        int end = Math.max(this.cursorPos, this.highlightPos);
        int availableSpace = this.maxLength - this.value.length() - (start - end);
        String filteredInput = ChatAllowedCharacters.filerAllowedCharacters(input);
        int inputLength = filteredInput.length();

        if (availableSpace < inputLength) {
            filteredInput = filteredInput.substring(0, availableSpace);
            inputLength = availableSpace;
        }

        String resultText = (new StringBuilder(this.value)).replace(start, end, filteredInput)
            .toString();
        if (this.filter.test(resultText)) {
            this.value = resultText;
            this.setCursorPosition(start + inputLength);
            this.setHighlightPos(this.cursorPos);
            this.onValueChange(this.value);
        }
    }

    private void onValueChange(String newValue) {
        if (this.responder != null) {
            this.responder.accept(newValue);
        }
    }

    private void deleteText(int direction) {
        if (GuiScreen.isCtrlKeyDown()) {
            this.deleteWords(direction);
        } else {
            this.deleteChars(direction);
        }
    }

    public void deleteWords(int count) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.deleteChars(this.getWordPosition(count) - this.cursorPos);
            }
        }
    }

    public void deleteChars(int count) {
        if (!this.value.isEmpty()) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                int targetPos = this.getCursorPos(count);
                int start = Math.min(targetPos, this.cursorPos);
                int end = Math.max(targetPos, this.cursorPos);
                if (start != end) {
                    String resultText = (new StringBuilder(this.value)).delete(start, end)
                        .toString();
                    if (this.filter.test(resultText)) {
                        this.value = resultText;
                        this.moveCursorTo(start);
                    }
                }
            }
        }
    }

    public int getWordPosition(int count) {
        return this.getWordPosition(count, this.getCursorPosition());
    }

    private int getWordPosition(int count, int startPosition) {
        return this.getWordPosition(count, startPosition, true);
    }

    private int getWordPosition(int count, int startPosition, boolean skipSpaces) {
        int currentPos = startPosition;
        boolean isBackward = count < 0;
        int absCount = Math.abs(count);

        for (int i = 0; i < absCount; ++i) {
            if (!isBackward) {
                int textLength = this.value.length();
                currentPos = this.value.indexOf(' ', currentPos);
                if (currentPos == -1) {
                    currentPos = textLength;
                } else {
                    while (skipSpaces && currentPos < textLength && this.value.charAt(currentPos) == ' ') {
                        ++currentPos;
                    }
                }
            } else {
                while (skipSpaces && currentPos > 0 && this.value.charAt(currentPos - 1) == ' ') {
                    --currentPos;
                }

                while (currentPos > 0 && this.value.charAt(currentPos - 1) != ' ') {
                    --currentPos;
                }
            }
        }

        return currentPos;
    }

    public void moveCursor(int p_94189_) {
        this.moveCursorTo(this.getCursorPos(p_94189_));
    }

    private int getCursorPos(int p_94221_) {
        return StringHelpers.offsetByCodepoints(this.value, this.cursorPos, p_94221_);
    }

    public void moveCursorTo(int p_94193_) {
        this.setCursorPosition(p_94193_);
        if (!this.shiftPressed) {
            this.setHighlightPos(this.cursorPos);
        }

        this.onValueChange(this.value);
    }

    public void setCursorPosition(int i) {
        this.cursorPos = MathHelper.clamp_int(i, 0, this.value.length());
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

    @Override
    public boolean charTyped(char typedChar, int modifiers) {
        if (!this.canConsumeInput()) {
            return false;
        } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            if (this.isEditable) {
                this.insertText(Character.toString(typedChar));
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean canConsumeInput() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    public void onClick(double p_279417_, double p_279437_) {
        int i = MathHelper.floor_double(p_279417_) - this.getX();
        if (this.bordered) {
            i -= 4;
        }

        String s = this.fontRenderer.trimStringToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
        this.moveCursorTo(
            this.fontRenderer.trimStringToWidth(s, i)
                .length() + this.displayPos);
    }

    @Override
    public void playDownSound(SoundHandler soundHandlerIn) {

    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (this.isVisible()) {
            if (this.isBordered()) {
                int i = this.isFocused() ? -1 : -6250336;
                drawRect(
                    this.getX() - 1,
                    this.getY() - 1,
                    this.getX() + this.width + 1,
                    this.getY() + this.height + 1,
                    i);
                drawRect(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
            }

            int i2 = this.isEditable ? this.textColor : this.textColorUneditable;
            int j = this.cursorPos - this.displayPos;
            int k = this.highlightPos - this.displayPos;
            String s = this.fontRenderer.trimStringToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused() && this.frame / 6 % 2 == 0 && flag;
            int l = this.bordered ? this.getX() + 4 : this.getX();
            int i1 = this.bordered ? this.getY() + (this.height - 8) / 2 : this.getY();
            int j1 = l;
            if (k > s.length()) {
                k = s.length();
            }

            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRenderer.drawStringWithShadow(s1, l, i1, i2);
            }

            boolean flag2 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            int k1 = j1;
            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (!s.isEmpty() && flag && j < s.length()) {
                this.fontRenderer.drawStringWithShadow(s.substring(j), j1, i1, i2);
            }

            if (this.hint != null && s.isEmpty() && !this.isFocused()) {
                this.fontRenderer.drawStringWithShadow(this.hint, j1, i1, i2);
            }

            if (!flag2 && this.suggestion != null) {
                this.fontRenderer.drawStringWithShadow(this.suggestion, k1 - 1, i1, -8355712);
            }

            if (flag1) {
                if (flag2) {
                    drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.fontRenderer.FONT_HEIGHT, -3092272);
                } else {
                    this.fontRenderer.drawStringWithShadow("_", k1, i1, i2);
                }
            }

            if (k != j) {
                int l1 = l + this.fontRenderer.getStringWidth(s.substring(0, k));
                this.renderHighlight(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
            }

        }
    }

    private void renderHighlight(int startX, int startY, int endX, int endY) {
        if (startX < endX) {
            int i = startX;
            startX = endX;
            endX = i;
        }

        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.getX() + this.width) {
            endX = this.getX() + this.width;
        }

        if (startX > this.getX() + this.width) {
            startX = this.getX() + this.width;
        }

        drawRect(startX, startY, endX, endY, -16776961);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.value.length() > maxLength) {
            this.value = this.value.substring(0, maxLength);
            this.onValueChange(this.value);
        }

    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public boolean isBordered() {
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

    private boolean isEditable() {
        return this.isEditable;
    }

    public void setEditable(boolean editable) {
        this.isEditable = editable;
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

    public void setCanLoseFocus(boolean p_94191_) {
        this.canLoseFocus = p_94191_;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean p_94195_) {
        this.visible = p_94195_;
    }

    public void setSuggestion(@Nullable String p_94168_) {
        this.suggestion = p_94168_;
    }

    public int getScreenX(int index) {
        return index > this.value.length() ? this.getX()
            : this.getX() + this.fontRenderer.getStringWidth(this.value.substring(0, index));
    }

    public void setHint(@Nullable String hint) {
        this.hint = hint;
    }
}
