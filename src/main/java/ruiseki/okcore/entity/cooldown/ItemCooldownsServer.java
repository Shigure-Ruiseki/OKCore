package ruiseki.okcore.entity.cooldown;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.network.packet.PacketCooldown;

public class ItemCooldownsServer extends ItemCooldowns {

    private final EntityPlayerMP player;

    public ItemCooldownsServer(EntityPlayerMP playerIn) {
        this.player = playerIn;
    }

    @Override
    protected void onCooldownStarted(ResourceLocation group, int duration) {
        super.onCooldownStarted(group, duration);
        OKCore._instance.getPacketHandler()
            .sendToPlayer(new PacketCooldown(group, duration), this.player);
    }

    @Override
    protected void onCooldownEnded(ResourceLocation group) {
        super.onCooldownEnded(group);
        OKCore._instance.getPacketHandler()
            .sendToPlayer(new PacketCooldown(group, 0), this.player);
    }
}
