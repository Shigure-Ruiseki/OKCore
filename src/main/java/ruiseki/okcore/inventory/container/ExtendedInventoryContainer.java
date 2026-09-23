package ruiseki.okcore.inventory.container;

import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.okcore.inventory.IGuiContainerProvider;

/**
 * An extended container.
 *
 * @author rubensworks
 */
@Deprecated
public abstract class ExtendedInventoryContainer extends ContainerExtended {

    protected IGuiContainerProvider guiProvider;

    /**
     * Make a new instance.
     *
     * @param inventory   The player inventory.
     * @param guiProvider The gui provider.
     */
    public ExtendedInventoryContainer(InventoryPlayer inventory, IGuiContainerProvider guiProvider) {
        super(null, inventory);
        this.guiProvider = guiProvider;
    }

    /**
     * Get the gui provider.
     *
     * @return The gui provider.
     */
    public IGuiContainerProvider getGuiProvider() {
        return guiProvider;
    }

    @Override
    public String getGuiModId() {
        return getGuiProvider().getModGui()
            .getModId();
    }

    @Override
    public int getGuiId() {
        return getGuiProvider().getGuiID();
    }
}
