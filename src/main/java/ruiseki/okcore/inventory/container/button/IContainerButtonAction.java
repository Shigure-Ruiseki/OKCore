package ruiseki.okcore.inventory.container.button;

import net.minecraft.inventory.Container;

/**
 * A server-side button action.
 *
 * @author rubensworks
 */
public interface IContainerButtonAction<C extends Container> {

    /**
     * Called when the button with the given id was clicked client-side.
     *
     * @param buttonId  The button id.
     * @param container The container in which the button was clicked.
     */
    public void onAction(String buttonId, C container);
}
