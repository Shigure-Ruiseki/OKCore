package ruiseki.okcore.guide.button;

import net.minecraft.client.gui.GuiButton;

import ruiseki.okcore.guide.gui.GuiBase;

public class ButtonBase extends GuiButton {

    public GuiBase guiBase;

    public ButtonBase(int id, int x, int y, GuiBase guiBase) {
        super(id, x, y, "");
        this.guiBase = guiBase;
    }
}
