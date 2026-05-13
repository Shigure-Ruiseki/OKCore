package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.guide.capability.IGuideHandler;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketSyncHome extends PacketCodec {

    @CodecField
    public int page;

    public PacketSyncHome() {
        this.page = -1;
    }

    public PacketSyncHome(int page) {
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
        if (cap != null && this.page != -1) {
            cap.setLastPos("", -1, this.page);
        }
    }
}
