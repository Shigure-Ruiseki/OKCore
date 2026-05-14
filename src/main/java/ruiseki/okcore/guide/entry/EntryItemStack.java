package ruiseki.okcore.guide.entry;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Entry;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class EntryItemStack extends Entry {

    public ItemStack stack;

    public EntryItemStack(List<IPage> pageList, String name, ItemStack stack, boolean unicode) {
        super(pageList, name, unicode);
        this.stack = stack;
    }

    public EntryItemStack(List<IPage> pageList, String name, ItemStack stack) {
        this(pageList, name, stack, false);
    }

    public EntryItemStack(String name, boolean unicode, ItemStack stack) {
        super(name, unicode);
        this.stack = stack;
    }

    public EntryItemStack(String name, ItemStack stack) {
        super(name);
        this.stack = stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth,
        int entryHeight, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        if (stack != null) GuiHelpers.drawScaledItemStack(stack, entryX + 2, entryY, 0.5F);

        super.drawExtras(
            book,
            category,
            entryX,
            entryY,
            entryWidth,
            entryHeight,
            mouseX,
            mouseY,
            guiBase,
            fontRendererObj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryItemStack that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (stack != null ? stack.hashCode() : 0);
        return result;
    }
}
