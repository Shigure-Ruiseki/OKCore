package ruiseki.okcore.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRenderable {

    void drawScreen(int mouseX, int mouseY, float partialTicks);
}
