package ruiseki.okcore.guide;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.IGuiHandler;
import ruiseki.okcore.guide.gui.GuiCategory;
import ruiseki.okcore.guide.gui.GuiEntry;
import ruiseki.okcore.guide.gui.GuiHome;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuideHelpers;

public class GuideGuiHandler implements IGuiHandler {

    public GuideGuiHandler() {}

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack bookStack = player.getHeldItem();

        if (bookStack != null && bookStack.getItem() instanceof IGuideItem) {
            Book book = GuideHelpers.getIndexedBooks()
                .get(ID);
            try {
                if (bookStack.hasTagCompound()) {
                    NBTTagCompound tagCompound = bookStack.getTagCompound();
                    if (tagCompound.hasKey(NBTBookTags.ENTRY_TAG) && tagCompound.hasKey(NBTBookTags.CATEGORY_TAG)) {
                        CategoryAbstract category = book.getCategories()
                            .get(tagCompound.getInteger(NBTBookTags.CATEGORY_TAG));
                        EntryAbstract entry = category.entries
                            .get(new ResourceLocation(tagCompound.getString(NBTBookTags.ENTRY_TAG)));
                        int pageNumber = tagCompound.getInteger(NBTBookTags.PAGE_TAG);
                        GuiEntry guiEntry = new GuiEntry(book, category, entry, player, bookStack);
                        guiEntry.pageNumber = pageNumber;
                        return guiEntry;
                    } else if (tagCompound.hasKey(NBTBookTags.CATEGORY_TAG)) {
                        CategoryAbstract category = book.getCategories()
                            .get(tagCompound.getInteger(NBTBookTags.CATEGORY_TAG));
                        int entryPage = tagCompound.getInteger(NBTBookTags.ENTRY_PAGE_TAG);
                        GuiCategory guiCategory = new GuiCategory(book, category, player, bookStack, null);
                        guiCategory.entryPage = entryPage;
                        return guiCategory;
                    } else {
                        int categoryNumber = tagCompound.getInteger(NBTBookTags.CATEGORY_PAGE_TAG);
                        GuiHome guiHome = new GuiHome(book, player, bookStack);
                        guiHome.categoryPage = categoryNumber;
                        return guiHome;
                    }
                }
            } catch (Exception ignore) {}

            return new GuiHome(book, player, bookStack);
        }

        return null;
    }
}
