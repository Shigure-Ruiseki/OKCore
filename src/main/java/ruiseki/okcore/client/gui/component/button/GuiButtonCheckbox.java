package ruiseki.okcore.client.gui.component.button;

import net.minecraft.client.Minecraft;

import ruiseki.okcore.client.gui.image.Image;
import ruiseki.okcore.client.gui.image.Images;

public class GuiButtonCheckbox extends GuiButtonExtended {

    private boolean checked;

    public GuiButtonCheckbox(int id, int x, int y, int width, int height, String string, OnPress onPress,
        boolean background) {
        super(id, x, y, width, height, string, onPress, background);
    }

    public GuiButtonCheckbox(int id, int x, int y, String string, OnPress onPress) {
        this(id, x, y, 10, 10, string, onPress, false);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            setChecked(!isChecked());
            return true;
        }
        return false;
    }

    @Override
    protected void drawButtonInner(Minecraft minecraft, int mouseX, int mouseY, boolean mouseOver) {
        if (visible) {
            // Determine image
            int i = 0;
            if (isChecked()) {
                i = 2;
            } else if (mouseOver) {
                i = 1;
            }
            Image image = Images.CHECKBOX[i];

            // Determine position
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            int x = this.width <= imageWidth ? this.xPosition : this.xPosition + (this.width - imageWidth) / 2;
            int y = this.height <= imageHeight ? this.yPosition : this.yPosition + (this.height - imageHeight) / 2;

            // Draw image
            image.draw(this, x, y);
        }
    }
}
