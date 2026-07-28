package ruiseki.okcore.inventory.container.button;

import net.minecraft.client.gui.Gui;
import net.minecraft.inventory.Container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A button action interface for the client.
 * 
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public interface IButtonActionClient<G extends Gui, C extends Container> extends IButtonAction<C> {

    /**
     * Called when clicked.
     * 
     * @param buttonId  The button id.
     * @param gui       The gui in which the button was clicked.
     * @param container The container in which the button was clicked.
     */
    public void onAction(int buttonId, G gui, C container);

}
