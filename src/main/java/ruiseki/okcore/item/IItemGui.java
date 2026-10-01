package ruiseki.okcore.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.inventory.IContainerConstructor;
import ruiseki.okcore.inventory.ItemLocation;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;

public interface IItemGui {

    @Nullable
    public IContainerConstructor getContainer(World world, EntityPlayer player, ItemLocation itemLocation);

    public abstract Class<? extends ContainerExtended> getContainerClass(World world, EntityPlayer player,
        ItemStack itemStack);

    /**
     * Write additional data to a packet buffer that will be sent to the client when opening the GUI.
     *
     * @param packetBuffer A packet buffer to write to.
     * @param world        The world.
     * @param player       The player.
     * @param itemLocation The item with its location.
     */
    default void writeExtraGuiData(ExtendedBuffer packetBuffer, World world, EntityPlayer player,
        ItemLocation itemLocation) {
        ItemLocation.writeToPacketBuffer(packetBuffer, itemLocation);
    }
}
