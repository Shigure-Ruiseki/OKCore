package ruiseki.okcore.guide.gui;

import java.awt.Color;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.guide.wrapper.EntryWrapper;
import ruiseki.okcore.network.packet.PacketSyncGuidePos;

public class GuiCategory extends GuiBase {

    public CategoryAbstract category;
    public HashMultimap<Integer, EntryWrapper> entryWrapperMap = HashMultimap.create();
    @Nullable
    public EntryAbstract startEntry;

    public GuiCategory(Book book, CategoryAbstract category, EntityPlayer player, @Nullable EntryAbstract startEntry) {
        super(book, player);
        this.category = category;
        this.startEntry = startEntry;
    }

    @Override
    public int getPageCount() {
        return entryWrapperMap.asMap()
            .size();
    }

    @Override
    public void initGui() {
        super.initGui();

        this.entryWrapperMap.clear();

        int topOffset = 5;
        int eX = pageLeft();
        int eY = pageTop() + topOffset;
        int i = 0;
        int pageNumber = 0;
        int startPageNumber = 0;
        List<EntryAbstract> entries = Lists.newArrayList(category.entries.values());
        for (EntryAbstract entry : entries) {
            entry.onInit(book, category, this, player());
            entryWrapperMap.put(
                pageNumber,
                new EntryWrapper(this, book, category, entry, eX, eY, pageWidth(), 10, player(), this.fontRendererObj));
            if (entry.equals(this.startEntry)) {
                this.startEntry = null;
                startPageNumber = pageNumber;
            }
            eY += 13;
            i++;

            if (i >= 11) {
                i = 0;
                eY = pageTop() + topOffset;
                pageNumber++;
            }
        }

        addButtons(true, true);
        this.setPage(startPageNumber);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        drawCenteredStringWithShadow(
            fontRendererObj,
            category.getLocalizedName(),
            pageXCenter(),
            screenTop() - 10,
            Color.WHITE.getRGB());

        super.drawScreen(mouseX, mouseY, renderPartialTicks);

        for (EntryWrapper wrapper : this.entryWrapperMap.get(currentPage())) {
            if (wrapper.canPlayerSee()) {
                wrapper.draw(mouseX, mouseY, this);
                wrapper.drawExtras(mouseX, mouseY, this);
            }
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                wrapper.onHoverOver(mouseX, mouseY);
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int typeofClick) {
        super.mouseClicked(mouseX, mouseY, typeofClick);

        for (EntryWrapper wrapper : this.entryWrapperMap.get(currentPage())) {
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                if (typeofClick == 0) wrapper.entry.onLeftClicked(book, category, mouseX, mouseY, player(), this);
                else if (typeofClick == 1) wrapper.entry.onRightClicked(book, category, mouseX, mouseY, player(), this);
            }
        }

        if (typeofClick == 1) this.mc.displayGuiScreen(new GuiHome(book, player()));
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_BACK || keyCode == this.mc.gameSettings.keyBindUseItem.getKeyCode())
            this.mc.displayGuiScreen(new GuiHome(book, player()));
        if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_RIGHT) && currentPage() + 1 < entryWrapperMap.asMap()
            .size()) nextPage();
        if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_LEFT) && currentPage() > 0) prevPage();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        OKCore.instance.getPacketHandler()
            .sendToServer(
                new PacketSyncGuidePos(
                    "",
                    book.getCategories()
                        .indexOf(category),
                    currentPage()));
    }
}
