package ruiseki.okcore.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

@FunctionalInterface
public interface IGuiConstructor {

    @Nullable
    Container createMenu(int windowId, InventoryPlayer playerInventory, EntityPlayer player);
}
