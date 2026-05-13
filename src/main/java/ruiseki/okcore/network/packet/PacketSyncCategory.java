package ruiseki.okcore.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import ruiseki.okcore.guide.IGuideItem;
import ruiseki.okcore.guide.NBTBookTags;
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
        ItemStack book = player.getHeldItem();

        if (book != null && book.getItem() instanceof IGuideItem) {
            if (this.category != -1 && this.page != -1) {
                if (!book.hasTagCompound()) book.setTagCompound(new NBTTagCompound());

                book.getTagCompound()
                    .setInteger(NBTBookTags.CATEGORY_TAG, this.category);
                book.getTagCompound()
                    .setInteger(NBTBookTags.ENTRY_PAGE_TAG, this.page);
                book.getTagCompound()
                    .removeTag(NBTBookTags.ENTRY_TAG);
            }
        }
    }
}
