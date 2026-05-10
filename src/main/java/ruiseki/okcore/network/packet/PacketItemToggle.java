package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.okcore.item.IItemToggle;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketItemToggle extends PacketCodec {

    @CodecField
    private int slot;

    public PacketItemToggle() {}

    public PacketItemToggle(int slot) {
        this.slot = slot;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        Container container = player.openContainer;
        if (container == null) container = player.inventoryContainer;

        if (this.slot < 0 || this.slot >= container.inventorySlots.size()) return;

        Slot slotObject = container.getSlot(this.slot);

        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stack = slotObject.getStack();

            if (stack != null && stack.getItem() instanceof IItemToggle toggle) {

                toggle.toggle(player, stack);
            }
        }
    }
}
