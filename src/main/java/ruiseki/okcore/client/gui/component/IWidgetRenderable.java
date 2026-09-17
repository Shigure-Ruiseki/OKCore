package ruiseki.okcore.client.gui.component;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IWidgetRenderable {

    public int getX();

    public int getY();

    public void setX(int x);

    public void setY(int y);

    public int getWidth();

    public int getHeight();

    public String getTooltip();

    public void drawScreen(int mouseX, int mouseY, float partialTicks);

    public void drawWidget(int mouseX, int mouseY, float partialTicks);
}
