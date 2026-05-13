package ruiseki.okcore.guide.entry;

import java.util.List;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Entry;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class EntryResourceLocation extends Entry {

    public ResourceLocation image;

    public EntryResourceLocation(List<IPage> pageList, String name, ResourceLocation resourceLocation,
        boolean unicode) {
        super(pageList, name, unicode);
        this.image = resourceLocation;
    }

    public EntryResourceLocation(List<IPage> pageList, String name, ResourceLocation resourceLocation) {
        this(pageList, name, resourceLocation, false);
    }

    public EntryResourceLocation(String name, boolean unicode, ResourceLocation image) {
        super(name, unicode);
        this.image = image;
    }

    public EntryResourceLocation(String name, ResourceLocation image) {
        super(name);
        this.image = image;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth,
        int entryHeight, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(image);
        GuiHelpers.drawSizedIconWithoutColor(entryX + 2, entryY, 16, 16, 1F);

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
        if (!(o instanceof EntryResourceLocation that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (image != null ? image.hashCode() : 0);
        return result;
    }
}
