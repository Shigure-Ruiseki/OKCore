package ruiseki.okcore.guide.category;

import java.util.Map;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.Category;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class CategoryResourceLocation extends Category {

    public ResourceLocation resourceLocation;

    public CategoryResourceLocation(Map<ResourceLocation, EntryAbstract> entries, String unlocCategoryName,
        ResourceLocation resourceLocation) {
        super(entries, unlocCategoryName);
        this.resourceLocation = resourceLocation;
    }

    public CategoryResourceLocation(String name, ResourceLocation resourceLocation) {
        super(name);
        this.resourceLocation = resourceLocation;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX,
        int mouseY, GuiBase guiBase, boolean drawOnLeft, RenderItem renderItem) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(resourceLocation);
        GuiHelpers.drawSizedIconWithoutColor(categoryX, categoryY, 48, 48, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX,
        int mouseY, GuiBase guiBase, boolean drawOnLeft, RenderItem renderItem) {
        if (canSee(guiBase.player)
            && GuiHelpers.isMouseBetween(mouseX, mouseY, categoryX, categoryY, categoryWidth, categoryHeight))
            guiBase.drawHoveringText(this.getTooltip(), mouseX, mouseY, Minecraft.getMinecraft().fontRenderer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryResourceLocation that)) return false;
        if (!super.equals(o)) return false;

        return Objects.equals(resourceLocation, that.resourceLocation);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (resourceLocation != null ? resourceLocation.hashCode() : 0);
        return result;
    }
}
