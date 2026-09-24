package ruiseki.okcore.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

import ruiseki.okcore.inventory.container.ContainerExtended;

@FunctionalInterface
public interface IGuiConstructor {

    @Nullable
    ContainerExtended createContainer(int windowId, InventoryPlayer playerInventory, EntityPlayer player);
}
