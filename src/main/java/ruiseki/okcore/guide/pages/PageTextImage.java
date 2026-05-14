package ruiseki.okcore.guide.pages;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Page;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class PageTextImage extends Page {

    public PageText pageText;
    public ResourceLocation image;
    public boolean drawAtTop;
    protected int textureWidth, textureHeight;
    private final boolean scale;
    private final int imageHeight = 64;
    private final int spacing = 14;

    public PageTextImage(String draw, ResourceLocation image, boolean drawAtTop, int textureWidth, int textureHeight,
        boolean scale) {
        this.pageText = new PageText(draw, drawAtTop ? 0 : imageHeight);
        this.image = image;
        this.drawAtTop = drawAtTop;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.scale = scale;
    }

    public PageTextImage(String draw, ResourceLocation image, boolean drawAtTop) {
        this(draw, image, drawAtTop, 64, 64, false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        int width = textureWidth;
        int height = textureHeight;
        if (scale) {
            float factor1 = (float) guiBase.pageWidth() / (float) textureWidth;
            float factor2 = (float) imageHeight / (float) textureHeight;
            float factor = Math.min(factor1, factor2);
            width = (int) (textureWidth * factor);
            height = (int) (textureHeight * factor);
        }

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(image);
        GuiHelpers.drawSizedIconWithoutColor(
            pageLeft + spacing + (guiBase.pageWidth() - width) / 2,
            pageTop + spacing + (!drawAtTop ? (guiBase.pageHeight() - height) - 4 : 0),
            width,
            height,
            1F);
        pageText.draw(
            book,
            category,
            entry,
            pageLeft,
            pageTop + (!drawAtTop ? 0 : height + 4),
            mouseX,
            mouseY,
            guiBase,
            fontRendererObj);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageTextImage that)) return false;
        if (!super.equals(o)) return false;

        if (drawAtTop != that.drawAtTop) return false;
        if (!Objects.equals(pageText, that.pageText)) return false;
        return Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = pageText != null ? pageText.hashCode() : 0;
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (drawAtTop ? 1 : 0);
        return result;
    }
}
