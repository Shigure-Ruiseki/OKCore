package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketSyncGuidePos extends PacketCodec {

    @CodecField
    public String entryName = "";
    @CodecField
    public int categoryIndex = -1;
    @CodecField
    public int pageIndex = 0;

    public PacketSyncGuidePos() {}

    public PacketSyncGuidePos(String entryName, int categoryIndex, int pageIndex) {
        this.entryName = entryName;
        this.categoryIndex = categoryIndex;
        this.pageIndex = pageIndex;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {}

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        EntityHelpers.getCapability(player, CapabilityGuide.GUIDE_CAPABILITY)
            .ifPresent(cap -> cap.setLastPos(this.entryName, this.categoryIndex, this.pageIndex));
    }

}
