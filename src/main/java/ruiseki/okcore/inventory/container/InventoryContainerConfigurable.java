package ruiseki.okcore.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.okcore.inventory.IGuiContainerProvider;
import ruiseki.okcore.inventory.IGuiContainerProviderConfigurable;

/**
 * A container for configurables.
 *
 * @author rubensworks
 */
public abstract class InventoryContainerConfigurable extends ExtendedInventoryContainer {

    /**
     * Make a new instance.
     *
     * @param inventory   The player inventory.
     * @param guiProvider The gui provider.
     */
    public InventoryContainerConfigurable(InventoryPlayer inventory, IGuiContainerProvider guiProvider) {
        super(inventory, guiProvider);
    }

    /**
     * Get the gui provider.
     *
     * @return The gui provider.
     */
    public IGuiContainerProviderConfigurable getGuiProvider() {
        return (IGuiContainerProviderConfigurable) super.getGuiProvider();
    }

}
