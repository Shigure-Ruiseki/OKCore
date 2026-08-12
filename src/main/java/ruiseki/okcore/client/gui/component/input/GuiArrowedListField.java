package ruiseki.okcore.client.gui.component.input;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import ruiseki.okcore.client.gui.component.button.GuiButtonArrow;

/**
 * A number field which by default only accepts positive numbers.
 *
 * @param <E> The element type
 * @author rubensworks
 */
public class GuiArrowedListField<E> extends GuiTextFieldExtended {

    private final boolean arrows;
    private GuiButtonArrow arrowLeft;
    private GuiButtonArrow arrowRight;
    private List<E> elements;
    private int activeElement;
    private IInputListener listener;

    public GuiArrowedListField(int componentId, FontRenderer fontrenderer, int x, int y, int width, int height,
        boolean arrows, boolean background, List<E> elements) {
        super(componentId, fontrenderer, x, y, width, height, background);
        this.arrows = arrows;

        if (this.arrows) {
            arrowLeft = new GuiButtonArrow(0, x, y - 1, GuiButtonArrow.Direction.WEST);
            arrowRight = new GuiButtonArrow(1, x + width, y - 1, GuiButtonArrow.Direction.EAST);
            arrowRight.xPosition -= arrowRight.width;
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
        if (elements == null || elements.isEmpty() || index < 0 || index >= elements.size()) {
            this.activeElement = -1;
            setText("");
        } else {
            this.activeElement = index;
            setText(activeElementToString(getActiveElement()));
        }
        if (listener != null) listener.onChanged();
    }

    public boolean setActiveElement(E element) {
        int index = this.elements.indexOf(element);
        if (index < 0) {
            return false;
        }
        setActiveElement(index);
        return true;
    }

    protected String activeElementToString(E element) {
        return element.toString();
    }

    public E getActiveElement() throws NumberFormatException {
        if (activeElement < 0 || activeElement >= elements.size()) {
            return null;
        }
        return elements.get(activeElement);
    }

    @Override
    public void drawTextBox(Minecraft minecraft, int mouseX, int mouseY) {
        int offsetX = 0;
        if (arrows) {
            arrowLeft.drawButton(minecraft, mouseX, mouseY);
            arrowRight.drawButton(minecraft, mouseX, mouseY);
            offsetX = arrowLeft.width;
            xPosition += offsetX + 1;
            width -= offsetX * 2;
        }
        super.drawTextBox(minecraft, mouseX, mouseY);
        if (arrows) {
            xPosition -= offsetX + 1;
            width += offsetX * 2;
        }
    }

    protected void increase() {
        if (elements == null || elements.isEmpty()) return;
        int nextIndex = (activeElement < 0) ? 0 : (activeElement + 1) % elements.size();
        setActiveElement(nextIndex);
    }

    protected void decrease() {
        if (elements == null || elements.isEmpty()) return;
        int prevIndex = (activeElement <= 0) ? elements.size() - 1 : activeElement - 1;
        setActiveElement(prevIndex);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (arrows && arrowRight != null && arrowRight.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            increase();
        } else if (arrows && arrowLeft != null && arrowLeft.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            decrease();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

}
