package ruiseki.okcore.guide.pages;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Page;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class PageImage extends Page {

    public ResourceLocation image;
    protected int textureWidth, textureHeight;
    protected boolean scale;

    public PageImage(ResourceLocation image, int textureWidth, int textureHeight, boolean scale) {
        this.image = image;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.scale = scale;
    }

    public PageImage(ResourceLocation image) {
        this(image, 64, 64, false);
    }

    @Override
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        int width = textureWidth;
        int height = textureHeight;

        if (scale) {
            float factor1 = (float) guiBase.pageWidth() / (float) textureWidth;
            float factor2 = (float) guiBase.pageHeight() / (float) textureHeight;
            float factor = Math.min(factor1, factor2);
            width = (int) (textureWidth * factor);
            height = (int) (textureHeight * factor);
        }

        int x = pageLeft + 14 + (guiBase.pageWidth() - width) / 2;
        int y = pageTop + 14 + (guiBase.pageHeight() - height) / 2;

        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(image);
        GuiHelpers.drawSizedIconWithoutColor(x, y, width, height, 1F);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageImage pageImage)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(image, pageImage.image);
    }

    @Override
    public int hashCode() {
        return image != null ? image.hashCode() : 0;
    }
}
