package ruiseki.okcore.client.gui.component;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IWidgetEventListener {

    default void onClick(double mouseX, double mouseY) {}

    default void onRelease(double mouseX, double mouseY) {}

    default void onDrag(double mouseX, double mouseY, double dragX, double dragY) {}
}
