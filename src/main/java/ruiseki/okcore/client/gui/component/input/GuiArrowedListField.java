package ruiseki.okcore.client.gui.component.input;

import java.util.List;

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

    public GuiArrowedListField(FontRenderer fontrenderer, int x, int y, int width, int height, boolean arrows,
        String narrationMessage, boolean background, List<E> elements) {
        super(fontrenderer, x, y, width, height, narrationMessage, background);
        this.arrows = arrows;

        if (this.arrows) {
            this.arrowLeft = new GuiButtonArrow(x, y - 1, btn -> decrease(), GuiButtonArrow.Direction.WEST);
            this.arrowRight = new GuiButtonArrow(x + width, y - 1, btn -> increase(), GuiButtonArrow.Direction.EAST);
            arrowRight.setX(arrowRight.getX() - arrowRight.getWidth());
        }
        setBordered(true);
        this.elements = elements;
        setActiveElement(0);
    }

    public void setListener(IInputListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isBordered() {
        return false; // We want the offset, but not the drawing itself.
    }

    public void setActiveElement(int index) {
        if (index >= elements.size()) {
            this.activeElement = -1;
            setValue("");
        } else {
            this.activeElement = index;
            setValue(activeElementToString(getActiveElement()));
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
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        int offsetX = 0;
        if (arrows) {
            arrowLeft.drawScreen(mouseX, mouseY, partialTicks);
            arrowRight.drawScreen(mouseX, mouseY, partialTicks);
            offsetX = arrowLeft.getWidth();
            setX(getX() + offsetX + 1);
            width -= offsetX * 2;
        }
        super.drawWidget(mouseX, mouseY, partialTicks);
        if (arrows) {
            setX(getX() - (offsetX + 1));
            width += offsetX * 2;
        }
    }

    protected void increase() {
        if (!elements.isEmpty()) {
            setActiveElement((activeElement + 1) % elements.size());
        }
    }

    protected void decrease() {
        if (!elements.isEmpty()) {
            setActiveElement((activeElement - 1 + elements.size()) % elements.size());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        return arrowLeft.mouseClicked(mouseX, mouseY, mouseButton)
            || arrowRight.mouseClicked(mouseX, mouseY, mouseButton)
            || super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
