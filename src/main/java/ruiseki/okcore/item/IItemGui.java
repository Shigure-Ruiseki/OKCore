package ruiseki.okcore.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.inventory.IGuiConstructor;
import ruiseki.okcore.inventory.container.ContainerExtended;
import ruiseki.okcore.network.ExtendedBuffer;

public interface IItemGui {

    @Nullable
    public IGuiConstructor getGuiProvider(World world, EntityPlayer player, int itemIndex);

    public abstract Class<? extends ContainerExtended> getContainerClass(World world, EntityPlayer player,
        ItemStack itemStack);

    /**
     * Write additional data to a packet buffer that will be sent to the client when opening the GUI.
     *
     * @param packetBuffer A packet buffer to write to.
     * @param world        The world.
     * @param player       The player.
     * @param itemIndex    The slot index in player inventory.
     */
    default void writeExtraGuiData(ExtendedBuffer packetBuffer, World world, EntityPlayer player, int itemIndex) {
        packetBuffer.writeInt(itemIndex);
    }
}
