package ruiseki.okcore.client.gui.component.button;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.image.Image;
import ruiseki.okcore.client.gui.image.Images;

@SideOnly(Side.CLIENT)
public class GuiButtonCheckbox extends GuiButton {

    private boolean checked;

    public GuiButtonCheckbox(int x, int y, int width, int height, String string, OnPress onPress) {
        super(x, y, width, height, string, onPress);
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public void onPress() {
        setChecked(!isChecked());
        super.onPress();
    }

    @Override
    public void drawWidget(int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            // Determine image
            int i = 0;
            if (isChecked()) {
                i = 2;
            } else if (isHovered()) {
                i = 1;
            }
            Image image = Images.CHECKBOX[i];

            // Determine position
            int imageWidth = image.getWidth();
            int imageWHeight = image.getHeight();
            int x = this.width <= imageWidth ? this.getX() : this.getX() + (this.width - imageWidth) / 2;
            int y = this.height <= imageWHeight ? this.getY() : this.getY() + (this.height - imageWHeight) / 2;

            // Draw image
            image.draw(this, x, y);
        }
    }
}
