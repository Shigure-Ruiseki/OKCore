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
        ItemStack book = player.getHeldItem();
        if (book != null && book.getItem() instanceof IGuideItem && this.page != -1) {
            if (!book.hasTagCompound()) book.setTagCompound(new NBTTagCompound());

            book.stackTagCompound.setInteger(NBTBookTags.CATEGORY_PAGE_TAG, this.page);
            book.stackTagCompound.removeTag(NBTBookTags.CATEGORY_TAG);
            book.stackTagCompound.removeTag(NBTBookTags.ENTRY_TAG);
        }
    }
}
