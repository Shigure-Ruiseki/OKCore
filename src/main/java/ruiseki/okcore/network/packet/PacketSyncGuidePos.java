package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.guide.capability.IGuideHandler;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketSyncGuidePos extends PacketCodec {

    @CodecField
    public String entryName;
    @CodecField
    public int categoryIndex;
    @CodecField
    public int pageIndex;

    public PacketSyncGuidePos() {
        this.entryName = "";
        this.categoryIndex = -1;
        this.pageIndex = 0;
    }

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
        IGuideHandler cap = EntityHelpers.getCapability(player, CapabilityGuide.GUIDE_CAPABILITY, null);
        if (cap != null) {
            cap.setLastPos(this.entryName, this.categoryIndex, this.pageIndex);
        }
    }

}
