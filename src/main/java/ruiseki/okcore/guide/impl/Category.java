package ruiseki.okcore.guide.impl;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.gui.GuiCategory;
import ruiseki.okcore.guide.gui.GuiHome;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;

public class Category extends CategoryAbstract {

    public Category(Map<ResourceLocation, EntryAbstract> entryList, String name) {
        super(entryList, name);
    }

    public Category(String name) {
        super(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX,
        int mouseY, GuiBase guiBase, boolean drawOnLeft, RenderItem renderItem) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, int categoryX, int categoryY, int categoryWidth, int categoryHeight, int mouseX,
        int mouseY, GuiBase guiBase, boolean drawOnLeft, RenderItem renderItem) {}

    @Override
    public boolean canSee(EntityPlayer player) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLeftClicked(Book book, int mouseX, int mouseY, EntityPlayer player) {
        Minecraft.getMinecraft()
            .displayGuiScreen(new GuiCategory(book, this, player, null));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRightClicked(Book book, int mouseX, int mouseY, EntityPlayer player) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void onInit(Book book, GuiHome guiHome, EntityPlayer player) {}
}
