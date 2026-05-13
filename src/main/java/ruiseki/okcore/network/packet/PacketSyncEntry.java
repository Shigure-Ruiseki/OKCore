package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import ruiseki.okcore.Reference;
import ruiseki.okcore.guide.IGuideItem;
import ruiseki.okcore.guide.NBTBookTags;
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
        ItemStack book = player.getHeldItem();
        if (book != null && book.getItem() instanceof IGuideItem) {
            if (this.category != -1 && !this.entry.equals(new ResourceLocation("guideapi", "none"))
                && this.page != -1) {
                if (!book.hasTagCompound()) book.setTagCompound(new NBTTagCompound());

                book.getTagCompound()
                    .setInteger(NBTBookTags.CATEGORY_TAG, this.category);
                book.getTagCompound()
                    .setString(NBTBookTags.ENTRY_TAG, this.entry.toString());
                book.getTagCompound()
                    .setInteger(NBTBookTags.PAGE_TAG, this.page);
            }
        }
    }
}
