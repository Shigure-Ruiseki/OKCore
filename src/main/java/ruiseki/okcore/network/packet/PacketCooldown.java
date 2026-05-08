package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketCooldown extends PacketCodec {

    @CodecField
    private ResourceLocation group;
    @CodecField
    private int ticks;

    public PacketCooldown() {}

    public PacketCooldown(ResourceLocation group, int ticksIn) {
        this.group = group;
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
                EntityHelpers.getItemCooldowns(player)
                    .removeCooldown(this.group);
            } else {
                EntityHelpers.getItemCooldowns(player)
                    .addCooldown(this.group, this.ticks);
            }
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
