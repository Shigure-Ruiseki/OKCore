package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketSyncCursorStack extends PacketCodec {

    @CodecField
    private ItemStack stack;

    public PacketSyncCursorStack() {}

    public PacketSyncCursorStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        player.inventory.setItemStack(this.stack);
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
