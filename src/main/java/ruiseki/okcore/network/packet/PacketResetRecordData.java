package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.audio.record.ClientRecordDataHandler;
import ruiseki.okcore.network.PacketCodec;

/**
 * S->C Reset Client to send new data to Server
 */
public class PacketResetRecordData extends PacketCodec {

    public PacketResetRecordData() {}

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        ClientRecordDataHandler.clear();
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {}
}
