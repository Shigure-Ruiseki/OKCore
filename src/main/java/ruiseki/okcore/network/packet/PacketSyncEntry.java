package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.Reference;
import ruiseki.okcore.guide.capability.CapabilityGuide;
import ruiseki.okcore.guide.capability.IGuideHandler;
import ruiseki.okcore.helper.EntityHelpers;
import ruiseki.okcore.network.CodecField;
import ruiseki.okcore.network.PacketCodec;

public class PacketSyncEntry extends PacketCodec {

    @CodecField
    public int category;
    @CodecField
    public ResourceLocation entry;
    @CodecField
    public int page;

    public PacketSyncEntry() {
        this.category = -1;
        this.entry = new ResourceLocation(Reference.MOD_ID, "none");
        this.page = -1;
    }

    public PacketSyncEntry(int category, ResourceLocation entry, int page) {
        this.category = category;
        this.entry = entry;
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
        if (cap != null && this.category != -1 && this.entry != null) {
            cap.setLastPos(this.entry.toString(), this.category, this.page);
        }
    }
}
