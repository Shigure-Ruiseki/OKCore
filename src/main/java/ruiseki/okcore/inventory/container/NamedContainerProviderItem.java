package ruiseki.okcore.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.inventory.IGuiConstructor;

public class NamedContainerProviderItem implements IGuiConstructor {

    private final int index;
    private final IContainerSupplier containerSupplier;

    public NamedContainerProviderItem(int index, IContainerSupplier containerSupplier) {
        this.index = index;
        this.containerSupplier = containerSupplier;
    }

    @Override
    public @Nullable ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory,
        EntityPlayer player) {
        return this.containerSupplier.create(windowId, playerInventory, this.index);
    }

    public static interface IContainerSupplier {

        public ContainerExtended create(int id, InventoryPlayer playerInventory, int index);
    }

}
