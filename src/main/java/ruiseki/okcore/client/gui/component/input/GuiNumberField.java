package ruiseki.okcore.client.gui.component.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import ruiseki.okcore.client.gui.component.button.GuiButtonArrow;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * A number field which by default only accepts positive numbers.
 *
 * @author rubensworks
 */
public class GuiNumberField extends GuiTextFieldExtended {

    private final boolean arrows;
    private GuiButtonArrow arrowUp;
    private GuiButtonArrow arrowDown;
    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;
    private boolean isEnabled = true;

    public GuiNumberField(int componentId, FontRenderer fontrenderer, int x, int y, int width, int height,
        boolean arrows, boolean background) {
        super(componentId, fontrenderer, x, y, width, height, background);
        this.arrows = arrows;

        if (this.arrows) {
            this.arrowUp = new GuiButtonArrow(0, x, y + height / 2, GuiButtonArrow.Direction.NORTH, btn -> increase());
            this.arrowDown = new GuiButtonArrow(
                1,
                x,
                y + height / 2,
                GuiButtonArrow.Direction.SOUTH,
                btn -> decrease());
            this.arrowUp.yPosition -= this.arrowUp.height;
        }
        setEnableBackgroundDrawing(true);
        setText("0");
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        if (this.arrows && this.arrowUp != null && this.arrowDown != null) {
            this.arrowUp.enabled = enabled;
            this.arrowDown.enabled = enabled;
            if (enabled) {
                updateArrowsState();
            }
        }
        super.setEnabled(enabled);
    }

    @Override
    public boolean getEnableBackgroundDrawing() {
        return false; // We want the offset, but not the drawing itself.
    }

    public void setPositiveOnly(boolean positiveOnly) {
        setMinValue(positiveOnly ? 0 : Integer.MIN_VALUE);
    }

    public int getMinValue() {
        return this.minValue;
    }

    /**
     * @param minValue The minimal value (inclusive)
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    /**
     * @param maxValue The maximal value (inclusive)
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getInt() throws NumberFormatException {
        return validateNumber(Integer.parseInt(getText()));
    }

    public double getDouble() throws NumberFormatException {
        return validateNumber(Double.parseDouble(getText()));
    }

    public float getFloat() throws NumberFormatException {
        return validateNumber(Float.parseFloat(getText()));
    }

    @Override
    public void drawTextBox(Minecraft minecraft, int mouseX, int mouseY) {
        int offsetX = 0;
        GlStateManager.color(1, 1, 1, 1);
        if (this.arrows) {
            if (this.arrowUp != null) this.arrowUp.drawButton(minecraft, mouseX, mouseY);
            if (this.arrowDown != null) this.arrowDown.drawButton(minecraft, mouseX, mouseY);
            offsetX = this.arrowUp != null ? this.arrowUp.width : 0;
            this.xPosition += offsetX;
            this.width -= offsetX;
        }
        super.drawTextBox(minecraft, mouseX, mouseY);
        if (this.arrows) {
            this.xPosition -= offsetX;
            this.width += offsetX;
        }
    }

    public int validateNumber(int number) {
        return Math.max(this.minValue, Math.min(this.maxValue, number));
    }

    public double validateNumber(double number) {
        return Math.max(this.minValue, Math.min(this.maxValue, number));
    }

    public float validateNumber(float number) {
        return Math.max((float) this.minValue, Math.min((float) this.maxValue, number));
    }

    protected int getDiffAmount() {
        return MinecraftHelpers.isShifted() ? 10 : 1;
    }

    protected void increase() {
        try {
            setText(Integer.toString(validateNumber(getInt() + getDiffAmount())));
        } catch (NumberFormatException e) {
            setText("0");
        }
    }

    protected void decrease() {
        try {
            setText(Integer.toString(validateNumber(getInt() - getDiffAmount())));
        } catch (NumberFormatException e) {
            setText("0");
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.isEnabled) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            updateArrowsState();
        }
    }

    @Override
    public void setText(String value) {
        super.setText(value);
        updateArrowsState();
    }

    @Override
    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        boolean ret = super.textboxKeyTyped(typedChar, keyCode);
        updateArrowsState();
        return ret;
    }

    protected void updateArrowsState() {
        if (this.arrows && this.arrowUp != null && this.arrowDown != null) {
            this.arrowDown.enabled = this.isEnabled;
            this.arrowUp.enabled = this.isEnabled;
            if (!this.isEnabled) return;

            try {
                int currentVal = getInt();
                if (currentVal <= this.minValue) {
                    this.arrowDown.enabled = false;
                }
                if (currentVal >= this.maxValue) {
                    this.arrowUp.enabled = false;
                }
            } catch (NumberFormatException e) {

            }
        }
    }

}
