package ruiseki.okcore.guide.impl.abstraction;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.gui.GuiCategory;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.helper.LangHelpers;

public abstract class EntryAbstract {

    public final List<IPage> pageList;
    public final String name;
    public boolean unicode;
    public ItemStack representativeStack;
    private Book ownerBook;
    private CategoryAbstract ownerCategory;

    public EntryAbstract(List<IPage> pageList, String name, boolean unicode) {
        this.pageList = pageList;
        this.name = name;
        this.unicode = unicode;
    }

    public EntryAbstract(List<IPage> pageList, String name) {
        this(pageList, name, false);
    }

    public EntryAbstract(String name, boolean unicode) {
        this(Lists.<IPage>newArrayList(), name, unicode);
    }

    public EntryAbstract(String name) {
        this(name, false);
    }

    public void addPage(IPage page) {
        this.pageList.add(page);
    }

    public void removePage(IPage page) {
        this.pageList.remove(page);
    }

    public void addPageList(List<IPage> pages) {
        this.pageList.addAll(pages);
    }

    public void removePageList(List<IPage> pages) {
        this.pageList.removeAll(pages);
    }

    public ItemStack getRepresentativeStack() {
        return representativeStack;
    }

    public void setRepresentativeStack(ItemStack stack) {
        this.representativeStack = stack;
    }

    public void setOwnerCategory(CategoryAbstract ownerCategory) {
        this.ownerCategory = ownerCategory;
    }

    public void setOwnerBook(Book ownerBook) {
        this.ownerBook = ownerBook;
    }

    public String getLocalizedName() {
        return LangHelpers.localize(name);
    }

    public Book getOwnerBook() {
        return ownerBook;
    }

    public CategoryAbstract getOwnerCategory() {
        return ownerCategory;
    }

    @SideOnly(Side.CLIENT)
    public abstract void draw(Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth,
        int entryHeight, int mouseX, int mouseY, GuiBase guiBase, FontRenderer renderer);

    @SideOnly(Side.CLIENT)
    public abstract void drawExtras(Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth,
        int entryHeight, int mouseX, int mouseY, GuiBase guiBase, FontRenderer renderer);

    public abstract boolean canSee(EntityPlayer player);

    @SideOnly(Side.CLIENT)
    public abstract void onLeftClicked(Book book, CategoryAbstract category, int mouseX, int mouseY,
        EntityPlayer player, GuiCategory guiCategory);

    @SideOnly(Side.CLIENT)
    public abstract void onRightClicked(Book book, CategoryAbstract category, int mouseX, int mouseY,
        EntityPlayer player, GuiCategory guiCategory);

    @SideOnly(Side.CLIENT)
    public abstract void onInit(Book book, CategoryAbstract category, GuiCategory guiCategory, EntityPlayer player);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntryAbstract that = (EntryAbstract) o;
        if (pageList != null ? !pageList.equals(that.pageList) : that.pageList != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pageList != null ? pageList.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
