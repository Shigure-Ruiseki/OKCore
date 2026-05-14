package ruiseki.okcore.guide.impl;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.gui.GuiCategory;
import ruiseki.okcore.guide.gui.GuiEntry;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class Entry extends EntryAbstract {

    public Entry(List<IPage> pageList, String name, boolean unicode) {
        super(pageList, name, unicode);
    }

    public Entry(List<IPage> pageList, String name) {
        super(pageList, name, false);
    }

    public Entry(String name, boolean unicode) {
        super(name, unicode);
    }

    public Entry(String name) {
        super(name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth, int entryHeight,
        int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {

        boolean startFlag = fontRendererObj.getUnicodeFlag();

        if (unicode) fontRendererObj.setUnicodeFlag(true);

        // Cutting code ripped from GuiButtonExt#drawButton(...)
        String entryName = getLocalizedName();
        int strWidth = fontRendererObj.getStringWidth(entryName);
        int ellipsisWidth = fontRendererObj.getStringWidth("...");

        if (strWidth > guiBase.screenWidth - 80 && strWidth > ellipsisWidth)
            entryName = fontRendererObj.trimStringToWidth(entryName, guiBase.screenWidth - 80 - ellipsisWidth)
                .trim() + "...";

        if (GuiHelpers.isMouseBetween(mouseX, mouseY, entryX, entryY, entryWidth, entryHeight)) {
            fontRendererObj.drawString(entryName, entryX + 12, entryY + 1, new Color(206, 206, 206).getRGB());
            fontRendererObj.drawString(entryName, entryX + 12, entryY, 0x423EBC);
        } else {
            fontRendererObj.drawString(entryName, entryX + 12, entryY, 0);
        }

        if (unicode && !startFlag) fontRendererObj.setUnicodeFlag(false);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawExtras(Book book, CategoryAbstract category, int entryX, int entryY, int entryWidth,
        int entryHeight, int mouseX, int mouseY, GuiBase guiBase, FontRenderer fontRendererObj) {
        boolean startFlag = fontRendererObj.getUnicodeFlag();
        fontRendererObj.setUnicodeFlag(false);

        // Cutting code ripped from GuiButtonExt#drawButton(...)
        int strWidth = fontRendererObj.getStringWidth(getLocalizedName());
        boolean cutString = false;

        if (strWidth > guiBase.screenWidth - 80 && strWidth > fontRendererObj.getStringWidth("...")) cutString = true;

        if (GuiHelpers.isMouseBetween(mouseX, mouseY, entryX, entryY, entryWidth, entryHeight) && cutString) {

            guiBase
                .drawHoveringText(Collections.singletonList(getLocalizedName()), entryX, entryY + 12, fontRendererObj);
            fontRendererObj.setUnicodeFlag(unicode);
        }

        fontRendererObj.setUnicodeFlag(startFlag);
    }

    @Override
    public boolean canSee(EntityPlayer player) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onLeftClicked(Book book, CategoryAbstract category, int mouseX, int mouseY, EntityPlayer player,
        GuiCategory guiCategory) {
        Minecraft.getMinecraft()
            .displayGuiScreen(new GuiEntry(book, category, this, player));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRightClicked(Book book, CategoryAbstract category, int mouseX, int mouseY, EntityPlayer player,
        GuiCategory guiCategory) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void onInit(Book book, CategoryAbstract category, GuiCategory guiCategory, EntityPlayer player) {}
}
