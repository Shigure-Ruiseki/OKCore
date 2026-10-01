package ruiseki.okcore.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.network.ExtendedBuffer;

/**
 * Represents an item location relative to a certain inventory extender.
 * 
 * @author rubensworks
 */
public record ItemLocation(IInventoryLocation inventoryLocation, int slot) {

    public ItemStack getItemStack(EntityPlayer player) {
        return this.inventoryLocation()
            .getItemInSlot(player, slot);
    }

    public void setItemStack(EntityPlayer player, ItemStack itemStack) {
        this.inventoryLocation()
            .setItemInSlot(player, slot, itemStack);
    }

    public static void writeToPacketBuffer(ExtendedBuffer packetBuffer, ItemLocation location) {
        packetBuffer.writeResourceLocation(
            location.inventoryLocation()
                .getUniqueName());
        packetBuffer.writeInt(location.slot());
    }

    public static ItemLocation readFromPacketBuffer(ExtendedBuffer packetBuffer) {
        IInventoryLocation inventoryLocation = InventoryLocations.REGISTRY.get(packetBuffer.readResourceLocation());
        int slot = packetBuffer.readInt();
        return new ItemLocation(inventoryLocation, slot);
    }

}
