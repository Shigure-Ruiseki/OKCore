package ruiseki.okcore.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.inventory.container.ContainerExtended;

@SideOnly(Side.CLIENT)
public interface IContainerAccess<T extends ContainerExtended> {

    T getContainer();
}
