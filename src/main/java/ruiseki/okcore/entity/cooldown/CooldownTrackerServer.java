package ruiseki.okcore.entity.cooldown;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.network.packet.PacketCooldown;

public class CooldownTrackerServer extends CooldownTracker {

    private final EntityPlayerMP player;

    public CooldownTrackerServer(EntityPlayerMP playerIn) {
        this.player = playerIn;
    }

    protected void notifyOnSet(Item itemIn, int ticksIn) {
        super.notifyOnSet(itemIn, ticksIn);
        OKCore.instance.getPacketHandler()
            .sendToPlayer(new PacketCooldown(itemIn, ticksIn), this.player);
    }

    protected void notifyOnRemove(Item itemIn) {
        super.notifyOnRemove(itemIn);
        OKCore.instance.getPacketHandler()
            .sendToPlayer(new PacketCooldown(itemIn, 0), this.player);
    }
}
