package ruiseki.okcore.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.inventory.ClickType;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * A container for an item.
 *
 * @author rubensworks
 *
 * @param <I> The item instance.
 */
public abstract class ItemInventoryContainer<I extends Item> extends ContainerExtended {

    protected I item;
    protected int itemIndex;

    /**
     * Make a new instance.
     *
     * @param inventory The player inventory.
     * @param itemIndex The index of the item in use inside the player inventory.
     */
    public ItemInventoryContainer(@Nullable ContainerType<?> type, InventoryPlayer inventory, int itemIndex) {
        super(type, inventory);
        this.item = (I) InventoryHelpers.getItemFromIndex(inventory.player, itemIndex)
            .getItem();
        this.itemIndex = itemIndex;
    }

    public static int readItemIndex(ExtendedBuffer packetBuffer) {
        return packetBuffer.readInt();
    }

    /**
     * Get the item instance.
     *
     * @return The item.
     */
    public I getItem() {
        return item;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        ItemStack item = getItemStack(player);
        return item != null && item.getItem() == getItem();
    }

    public ItemStack getItemStack(EntityPlayer player) {
        return InventoryHelpers.getItemFromIndex(player, itemIndex);
    }

    @Override
    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y) {

            @Override
            public boolean canTakeStack(EntityPlayer player) {
                return this.getStack() != InventoryHelpers.getItemFromIndex(player, itemIndex);
            }

        };
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, ClickType clickType, EntityPlayer player) {
        if (clickType == ClickType.SWAP && clickedButton == itemIndex) {
            // Don't allow swapping with the slot of the active item.
            return ItemHelpers.EMPTY;
        }
        return super.slotClick(slotId, clickedButton, clickType, player);
    }
}
