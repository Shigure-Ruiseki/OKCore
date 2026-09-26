package ruiseki.okcore.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import ruiseki.okcore.client.gui.ContainerType;

/**
 * A container with inventory.
 *
 * @author rubensworks
 */
public abstract class InventoryContainer extends ContainerExtended {

    protected final IInventory inventory;

    /**
     * Make a new InventoryContainer.
     *
     * @param inventory The player inventory.
     */
    public InventoryContainer(ContainerType<?> containerType, InventoryPlayer playerInventory, IInventory inventory) {
        super(containerType, playerInventory);
        this.inventory = inventory;
        this.inventory.openInventory();
    }

    public IInventory getContainerInventory() {
        return inventory;
    }

    @Override
    protected int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        this.inventory.closeInventory();
    }
}
