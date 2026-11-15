package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.audio.record.RecordData;
import ruiseki.okcore.audio.record.ServerRecordDataHandler;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

/**
 * C -> S Client send data to Server
 */
public class PacketRecordSoundData extends PacketCodec {

    @CodecField
    private String recordName = "";

    @CodecField
    private RecordData recordData = null;

    public PacketRecordSoundData() {}

    public PacketRecordSoundData(String recordName, RecordData recordData) {
        this.recordName = recordName != null ? recordName : "";
        this.recordData = recordData;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {}

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        if (player == null || recordName == null || recordName.isEmpty() || recordData == null) return;
        if (!recordData.isValid()) return;
        ServerRecordDataHandler.putData(player.getUniqueID(), recordName, recordData);
    }
}
