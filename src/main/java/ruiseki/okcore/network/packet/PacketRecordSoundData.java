package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.client.audio.RecordData;
import ruiseki.okcore.client.audio.RecordDataRegistry;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketRecordSoundData extends PacketCodec {

    @CodecField
    private String recordName;

    @CodecField
    private RecordData recordData;

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
        if (recordName == null || recordName.isEmpty() || recordData == null) return;
        RecordDataRegistry.putData(recordName, recordData);
    }
}
