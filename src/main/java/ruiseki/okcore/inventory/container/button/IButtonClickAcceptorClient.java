package ruiseki.okcore.inventory.container.button;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A client side version of {@link IButtonClickAcceptor}.
 * 
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public interface IButtonClickAcceptorClient<G extends Gui, C extends Container>
    extends IButtonClickAcceptor<IButtonActionClient<G, C>> {

}
