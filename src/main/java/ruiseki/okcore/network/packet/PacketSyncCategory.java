package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.guide.capability.IGuideHandler;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketSyncCategory extends PacketCodec {

    @CodecField
    public int category;
    @CodecField
    public int page;

    public PacketSyncCategory() {
        this.category = -1;
        this.page = -1;
    }

    public PacketSyncCategory(int category, int page) {
        this.category = category;
        this.page = page;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        IGuideHandler cap = EntityHelpers.getCapability(player, CapabilityGuide.GUIDE_CAPABILITY, null);
        if (cap != null && this.category != -1) {
            cap.setLastPos("", this.category, this.page);
        }
    }
}
