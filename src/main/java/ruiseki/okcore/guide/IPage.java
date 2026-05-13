package ruiseki.okcore.guide;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.gui.GuiEntry;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;

public interface IPage {

    @SideOnly(Side.CLIENT)
    void draw(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj);

    @SideOnly(Side.CLIENT)
    void drawExtras(Book book, CategoryAbstract category, EntryAbstract entry, int guiLeft, int guiTop, int mouseX,
        int mouseY, GuiBase guiBase, FontRenderer fontRendererObj);

    boolean canSee(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack,
        GuiEntry guiEntry);

    @SideOnly(Side.CLIENT)
    void onLeftClicked(Book book, CategoryAbstract category, EntryAbstract entry, int mouseX, int mouseY,
        EntityPlayer player, GuiEntry guiEntry);

    @SideOnly(Side.CLIENT)
    void onRightClicked(Book book, CategoryAbstract category, EntryAbstract entry, int mouseX, int mouseY,
        EntityPlayer player, GuiEntry guiEntry);

    @SideOnly(Side.CLIENT)
    void onInit(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player, ItemStack bookStack,
        GuiEntry guiEntry);
}
