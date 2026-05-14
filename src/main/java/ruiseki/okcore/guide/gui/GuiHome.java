package ruiseki.okcore.guide.gui;

import java.awt.Color;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.HashMultimap;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.wrapper.CategoryWrapper;
import ruiseki.okcore.network.packet.PacketSyncGuidePos;

public class GuiHome extends GuiBase {

    public HashMultimap<Integer, CategoryWrapper> categoryWrapperMap = HashMultimap.create();

    public GuiHome(Book book, EntityPlayer player) {
        super(book, player);
    }

    @Override
    public int getPageCount() {
        return categoryWrapperMap.asMap()
            .size();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.categoryWrapperMap.clear();

        int cX = pageLeft() + 45;
        int cY = pageTop() + 40;
        int i = 0;
        int pageNumber = 0;

        for (CategoryAbstract category : book.getCategories()) {
            if (category.entries.isEmpty()) continue;

            category.onInit(book, this, player);
            int x = i % 5;
            int y = i / 5;
            categoryWrapperMap.put(
                pageNumber,
                new CategoryWrapper(
                    book,
                    category,
                    cX + x * 27,
                    cY + y * 30,
                    23,
                    23,
                    player(),
                    this.fontRendererObj,
                    itemRender,
                    false));
            i++;

            if (i >= 20) {
                i = 0;
                pageNumber++;
            }
        }

        addButtons(false, true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        super.drawScreen(mouseX, mouseY, renderPartialTicks);

        drawCenteredString(
            fontRendererObj,
            I18n.format(book.getHeader())
                .replace("\\n", "\n")
                .replace("&", "\u00a7"),
            pageXCenter(),
            pageTop(),
            0);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(currentPage()))
            if (wrapper.canPlayerSee()) wrapper.draw(mouseX, mouseY, this);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(currentPage()))
            if (wrapper.canPlayerSee()) wrapper.drawExtras(mouseX, mouseY, this);

        drawCenteredStringWithShadow(
            fontRendererObj,
            I18n.format(book.getTitle()),
            pageXCenter(),
            screenTop() - 10,
            Color.WHITE.getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int typeofClick) {
        super.mouseClicked(mouseX, mouseY, typeofClick);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(currentPage())) {
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                if (typeofClick == 0) wrapper.category.onLeftClicked(book, mouseX, mouseY, player);

                else if (typeofClick == 1) wrapper.category.onRightClicked(book, mouseX, mouseY, player);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_RIGHT)
            && currentPage() + 1 < categoryWrapperMap.asMap()
                .size())
            nextPage();

        if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_LEFT) && currentPage() > 0) prevPage();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        OKCore.instance.getPacketHandler()
            .sendToServer(new PacketSyncGuidePos("", -1, currentPage()));
    }

    @Override
    protected void goBack() {

    }
}
