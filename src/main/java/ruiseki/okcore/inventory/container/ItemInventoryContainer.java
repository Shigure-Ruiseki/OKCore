package ruiseki.okcore.inventory.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.client.gui.ContainerType;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.inventory.ClickType;
import ruiseki.okcore.inventory.InteractionHand;
import ruiseki.okcore.inventory.InventoryLocationPlayer;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.network.ExtendedBuffer;

/**
 * A container for an item.
 *
 * Implementations of this class will typically have two constructors,
 * which will look something like this:
 * 
 * <pre>
 * 
 * // Called by the client-side screen factory
 * public MyContainer(int id, InventoryPlayer inventory, FriendlyByteBuf packetBuffer) {
 *     this(id, inventory, readItemIndex(packetBuffer), readHand(packetBuffer));
 * }
 *
 * // Called by the server-side container provider
 * public MyContainer(int id, InventoryPlayer inventory, int itemIndex, Hand hand) {
 *     super(RegistryEntries.CONTAINER_MY, id, inventory, itemIndex, hand);
 * }
 * </pre>
 *
 * @param <I> The item instance.
 * @author rubensworks
 */
public abstract class ItemInventoryContainer<I extends Item> extends ContainerExtended {

    protected I item;
    protected ItemLocation itemLocation;

    /**
     * Make a new instance.
     * 
     * @param type         The container type.
     * @param id           The container id.
     * @param inventory    The player inventory.
     * @param itemLocation The item location.
     */
    public ItemInventoryContainer(@Nullable ContainerType<?> type, int id, InventoryPlayer inventory,
        ItemLocation itemLocation) {
        super(type, id, inventory);
        this.item = (I) itemLocation.getItemStack(inventory.player)
            .getItem();
        this.itemLocation = itemLocation;
    }

    public static int readItemIndex(ExtendedBuffer packetBuffer) {
        return packetBuffer.readInt();
    }

    public static InteractionHand readHand(ExtendedBuffer packetBuffer) {
        return packetBuffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
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
        return this.itemLocation.getItemStack(player);
    }

    @Override
    protected Slot createNewSlot(IInventory inventory, int index, int x, int y) {
        return new Slot(inventory, index, x, y) {

            @Override
            public boolean canTakeStack(EntityPlayer player) {
                return this.getStack() != itemLocation.getItemStack(player);
            }

        };
    }

    @Override
    public ItemStack slotClick(int slotId, int clickedButton, ClickType clickType, EntityPlayer player) {
        if (clickType == ClickType.SWAP && itemLocation.inventoryLocation() == InventoryLocationPlayer.getInstance()
            && clickedButton == itemLocation.slot()) {
            // Don't allow swapping with the slot of the active item.
            return ItemHelpers.EMPTY;
        }
        return super.slotClick(slotId, clickedButton, clickType, player);
    }
}
