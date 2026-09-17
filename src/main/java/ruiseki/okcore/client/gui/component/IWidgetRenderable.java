package ruiseki.okcore.client.gui.component;

import net.minecraft.client.gui.inventory.GuiContainer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IWidgetRenderable {

    public int getX();

    public int getY();

    public void setX(int x);

    public void setY(int y);

    public void drawScreen(GuiContainer gui, int mouseX, int mouseY, float partialTicks);

    public void drawWidget(GuiContainer gui, int mouseX, int mouseY, float partialTicks);

    public void drawToolTips(GuiContainer gui, int mouseX, int mouseY, float partialTicks);
}
