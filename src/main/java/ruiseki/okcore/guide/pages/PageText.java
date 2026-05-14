package ruiseki.okcore.guide.pages;

import java.util.Objects;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Page;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.PageHelpers;

public class PageText extends Page {

    public String draw;
    private int yOffset;

    /**
     * @param draw    - Text to draw. Checks for localization.
     * @param yOffset - How many pixels to offset the text on the Y value
     */
    public PageText(String draw, int yOffset) {
        this.draw = draw;
        this.yOffset = yOffset;
    }

    public PageText(String draw) {
        this(draw, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        PageHelpers.drawFormattedText(
            pageLeft - 39 + 56,
            pageTop - 13 + 27 + yOffset,
            guiBase,
            I18n.format(draw),
            book.getTextColor()
                .getRGB());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageText pageText)) return false;
        if (!super.equals(o)) return false;

        if (yOffset != pageText.yOffset) return false;
        return Objects.equals(draw, pageText.draw);
    }

    @Override
    public int hashCode() {
        int result = draw != null ? draw.hashCode() : 0;
        result = 31 * result + yOffset;
        return result;
    }
}
