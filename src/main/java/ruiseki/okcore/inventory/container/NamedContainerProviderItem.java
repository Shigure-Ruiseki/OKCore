package ruiseki.okcore.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.inventory.IContainerConstructor;
import ruiseki.okcore.inventory.ItemLocation;

public class NamedContainerProviderItem implements IContainerConstructor {

    private final ItemLocation itemLocation;
    private final IContainerSupplier containerSupplier;

    public NamedContainerProviderItem(ItemLocation itemLocation, IContainerSupplier containerSupplier) {
        this.itemLocation = itemLocation;
        this.containerSupplier = containerSupplier;
    }

    @Override
    public @Nullable ContainerExtended createContainer(int id, InventoryPlayer playerInventory, EntityPlayer player) {
        return this.containerSupplier.create(id, playerInventory, itemLocation);
    }

    public static interface IContainerSupplier {

        public ContainerExtended create(int id, InventoryPlayer playerInventory, ItemLocation itemLocation);
    }

}
