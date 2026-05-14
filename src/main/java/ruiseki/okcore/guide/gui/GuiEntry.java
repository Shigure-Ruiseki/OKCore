package ruiseki.okcore.guide.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.guide.wrapper.PageWrapper;
import ruiseki.okcore.network.packet.PacketSyncGuidePos;

public class GuiEntry extends GuiBase {

    public CategoryAbstract category;
    public EntryAbstract entry;
    public List<PageWrapper> pageWrapperList = new ArrayList<PageWrapper>();

    public GuiEntry(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player) {
        super(book, player);
        this.category = category;
        this.entry = entry;
    }

    @Override
    public void initGui() {
        super.initGui();
        entry.onInit(book, category, null, player());

        this.pageWrapperList.clear();

        for (IPage page : this.entry.pageList) {
            page.onInit(book, category, entry, player(), this);
            pageWrapperList.add(
                new PageWrapper(
                    this,
                    book,
                    category,
                    entry,
                    page,
                    screenLeft(),
                    screenTop(),
                    player(),
                    this.fontRendererObj));
        }
        addButtons(true, true);
    }

    @Override
    public int getPageCount() {
        return pageWrapperList.size();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        drawCenteredStringWithShadow(
            fontRendererObj,
            entry.getLocalizedName(),
            pageXCenter(),
            screenTop() - 10,
            Color.WHITE.getRGB());

        super.drawScreen(mouseX, mouseY, renderPartialTicks);

        if (currentPage() < pageWrapperList.size()) {
            if (pageWrapperList.get(currentPage())
                .canPlayerSee()) {
                pageWrapperList.get(currentPage())
                    .draw(mouseX, mouseY, this);
                pageWrapperList.get(currentPage())
                    .drawExtras(mouseX, mouseY, this);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int typeofClick) {
        super.mouseClicked(mouseX, mouseY, typeofClick);
        for (PageWrapper wrapper : this.pageWrapperList) {
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                if (typeofClick == 0) {
                    pageWrapperList.get(currentPage()).page
                        .onLeftClicked(book, category, entry, mouseX, mouseY, player(), this);
                }
                if (typeofClick == 1) {
                    pageWrapperList.get(currentPage()).page
                        .onRightClicked(book, category, entry, mouseX, mouseY, player(), this);
                }
            }
        }

        if (typeofClick == 1) {
            this.mc.displayGuiScreen(new GuiCategory(book, category, player(), entry));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_BACK || keyCode == this.mc.gameSettings.keyBindUseItem.getKeyCode())
            this.mc.displayGuiScreen(new GuiCategory(book, category, player(), entry));
        if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_RIGHT) && currentPage() + 1 < pageWrapperList.size())
            nextPage();
        if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_LEFT) && currentPage() > 0) prevPage();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        ResourceLocation key = null;
        for (Map.Entry<ResourceLocation, EntryAbstract> mapEntry : category.entries.entrySet()) {
            if (mapEntry.getValue()
                .equals(entry)) {
                key = mapEntry.getKey();
                break;
            }
        }

        if (key != null) OKCore.instance.getPacketHandler()
            .sendToServer(
                new PacketSyncGuidePos(
                    key.toString(),
                    book.getCategories()
                        .indexOf(category),
                    currentPage()));
    }

    @Override
    protected void goBack() {
        this.mc.displayGuiScreen(new GuiCategory(book, category, player(), entry));
    }
}
