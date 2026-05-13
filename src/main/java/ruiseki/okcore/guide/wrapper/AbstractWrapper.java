package ruiseki.okcore.guide.wrapper;

import ruiseki.okcore.guide.gui.GuiBase;

public abstract class AbstractWrapper {

    public abstract void onHoverOver(int mouseX, int mouseY);

    public abstract boolean canPlayerSee();

    public abstract void draw(int mouseX, int mouseY, GuiBase gui);

    public abstract void drawExtras(int mouseX, int mouseY, GuiBase gui);

    public abstract boolean isMouseOnWrapper(int mouseX, int mouseY);
}
