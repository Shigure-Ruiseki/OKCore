package ruiseki.okcore.guide.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.gui.GuiEntry;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;

public class Page implements IPage {

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int pageLeft, int pageTop,
        int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {}

    @Override
    public boolean canSee(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player,
        GuiEntry guiEntry) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLeftClicked(Book book, CategoryAbstract category, EntryAbstract entry, int mouseX, int mouseY,
        EntityPlayer player, GuiEntry guiEntry) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void onRightClicked(Book book, CategoryAbstract category, EntryAbstract entry, int mouseX, int mouseY,
        EntityPlayer player, GuiEntry guiEntry) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void onInit(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player,
        GuiEntry guiEntry) {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        return true;
    }
}
