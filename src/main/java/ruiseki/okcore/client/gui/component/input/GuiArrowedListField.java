package ruiseki.okcore.client.gui.component.input;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import ruiseki.okcore.client.gui.component.button.GuiButtonArrow;

/**
 * A list field with navigation arrows.
 *
 * @param <E> The element type
 * @author rubensworks
 */
public class GuiArrowedListField<E> extends GuiTextFieldExtended {

    private final boolean arrows;
    private GuiButtonArrow arrowLeft;
    private GuiButtonArrow arrowRight;
    private List<E> elements;
    private int activeElement = -1;
    private IInputListener listener;

    public GuiArrowedListField(int componentId, FontRenderer fontrenderer, int x, int y, int width, int height,
        boolean arrows, boolean background, List<E> elements) {
        super(componentId, fontrenderer, x, y, width, height, background);
        this.arrows = arrows;

        if (this.arrows) {
            this.arrowLeft = new GuiButtonArrow(0, x, y - 1, GuiButtonArrow.Direction.WEST, btn -> decrease());
            this.arrowRight = new GuiButtonArrow(1, x + width, y - 1, GuiButtonArrow.Direction.EAST, btn -> increase());
            this.arrowRight.xPosition -= this.arrowRight.width;
        }
        setEnableBackgroundDrawing(true);
        this.elements = elements;
        setActiveElement(0);
    }

    public void setListener(IInputListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean getEnableBackgroundDrawing() {
        return false; // We want the offset, but not the drawing itself.
    }

    public void setActiveElement(int index) {
        if (this.elements == null || this.elements.isEmpty() || index < 0 || index >= this.elements.size()) {
            this.activeElement = -1;
            setText("");
        } else {
            this.activeElement = index;
            setText(activeElementToString(getActiveElement()));
        }
        if (this.listener != null) {
            this.listener.onChanged();
        }
    }

    public boolean setActiveElement(E element) {
        if (this.elements == null) return false;
        int index = this.elements.indexOf(element);
        if (index < 0) {
            return false;
        }
        setActiveElement(index);
        return true;
    }

    protected String activeElementToString(E element) {
        return element != null ? element.toString() : "";
    }

    public E getActiveElement() {
        if (this.elements == null || this.activeElement < 0 || this.activeElement >= this.elements.size()) {
            return null;
        }
        return this.elements.get(this.activeElement);
    }

    @Override
    public void drawTextBox(Minecraft minecraft, int mouseX, int mouseY) {
        int offsetX = 0;
        if (this.arrows) {
            if (this.arrowLeft != null) this.arrowLeft.drawButton(minecraft, mouseX, mouseY);
            if (this.arrowRight != null) this.arrowRight.drawButton(minecraft, mouseX, mouseY);
            offsetX = this.arrowLeft != null ? this.arrowLeft.width : 0;
            this.xPosition += offsetX + 1;
            this.width -= offsetX * 2;
        }
        super.drawTextBox(minecraft, mouseX, mouseY);
        if (this.arrows) {
            this.xPosition -= offsetX + 1;
            this.width += offsetX * 2;
        }
    }

    protected void increase() {
        if (this.elements == null || this.elements.isEmpty()) return;
        int nextIndex = (this.activeElement < 0) ? 0 : (this.activeElement + 1) % this.elements.size();
        setActiveElement(nextIndex);
    }

    protected void decrease() {
        if (this.elements == null || this.elements.isEmpty()) return;
        int prevIndex = (this.activeElement <= 0) ? this.elements.size() - 1 : this.activeElement - 1;
        setActiveElement(prevIndex);
    }
}
