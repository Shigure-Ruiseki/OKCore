package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketCooldown extends PacketCodec {

    @CodecField
    private Item itemId;
    @CodecField
    private int ticks;

    public PacketCooldown() {}

    public PacketCooldown(Item itemIn, int ticksIn) {
        this.itemId = itemIn;
        this.ticks = ticksIn;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        if (player != null) {
            if (this.ticks == 0) {
                EntityHelpers.getCooldownTracker(player)
                    .removeCooldown(this.itemId);
            } else {
                EntityHelpers.getCooldownTracker(player)
                    .setCooldown(this.itemId, this.ticks);
            }
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
