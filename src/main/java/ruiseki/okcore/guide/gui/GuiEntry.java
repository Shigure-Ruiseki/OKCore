package ruiseki.okcore.guide.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.button.ButtonBack;
import ruiseki.okcore.guide.button.ButtonNext;
import ruiseki.okcore.guide.button.ButtonPrev;
import ruiseki.okcore.guide.button.ButtonSearch;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.guide.wrapper.PageWrapper;
import ruiseki.okcore.network.packet.PacketSyncEntry;

public class GuiEntry extends GuiBase {

    public ResourceLocation outlineTexture;
    public ResourceLocation pageTexture;
    public Book book;
    public CategoryAbstract category;
    public EntryAbstract entry;
    public List<PageWrapper> pageWrapperList = new ArrayList<PageWrapper>();
    public ButtonBack buttonBack;
    public ButtonNext buttonNext;
    public ButtonPrev buttonPrev;
    public ButtonSearch buttonSearch;
    public int pageNumber;

    public GuiEntry(Book book, CategoryAbstract category, EntryAbstract entry, EntityPlayer player) {
        super(player);
        this.book = book;
        this.category = category;
        this.entry = entry;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
        this.pageNumber = 0;
    }

    @Override
    public void initGui() {
        super.initGui();
        entry.onInit(book, category, null, player);
        this.buttonList.clear();
        this.pageWrapperList.clear();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        this.buttonList.add(buttonBack = new ButtonBack(0, guiLeft + xSize / 6, guiTop, this));
        this.buttonList.add(buttonNext = new ButtonNext(1, guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, this));
        this.buttonList.add(buttonPrev = new ButtonPrev(2, guiLeft + xSize / 5, guiTop + 5 * ySize / 6, this));
        this.buttonList.add(buttonSearch = new ButtonSearch(3, (guiLeft + xSize / 6) - 25, guiTop + 5, this));

        for (IPage page : this.entry.pageList) {
            page.onInit(book, category, entry, player, this);
            pageWrapperList
                .add(new PageWrapper(this, book, category, entry, page, guiLeft, guiTop, player, this.fontRendererObj));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(pageTexture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(outlineTexture);
        drawTexturedModalRectWithColor(guiLeft, guiTop, 0, 0, xSize, ySize, book.getColor());

        pageNumber = MathHelper.clamp_int(pageNumber, 0, pageWrapperList.size() - 1);

        if (pageNumber < pageWrapperList.size()) {
            if (pageWrapperList.get(pageNumber)
                .canPlayerSee()) {
                pageWrapperList.get(pageNumber)
                    .draw(mouseX, mouseY, this);
                pageWrapperList.get(pageNumber)
                    .drawExtras(mouseX, mouseY, this);
            }
        }

        drawCenteredString(
            fontRendererObj,
            String.format("%d/%d", pageNumber + 1, pageWrapperList.size()),
            guiLeft + xSize / 2,
            guiTop + 5 * ySize / 6,
            0);
        drawCenteredStringWithShadow(
            fontRendererObj,
            entry.getLocalizedName(),
            guiLeft + xSize / 2,
            guiTop - 10,
            Color.WHITE.getRGB());

        buttonPrev.visible = pageNumber != 0;
        buttonNext.visible = pageNumber != pageWrapperList.size() - 1 && !pageWrapperList.isEmpty();

        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int typeofClick) {
        super.mouseClicked(mouseX, mouseY, typeofClick);
        for (PageWrapper wrapper : this.pageWrapperList) {
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                if (typeofClick == 0) {
                    pageWrapperList.get(pageNumber).page
                        .onLeftClicked(book, category, entry, mouseX, mouseY, player, this);
                }
                if (typeofClick == 1) {
                    pageWrapperList.get(pageNumber).page
                        .onRightClicked(book, category, entry, mouseX, mouseY, player, this);
                }
            }
        }

        if (typeofClick == 1) {
            this.mc.displayGuiScreen(new GuiCategory(book, category, player, entry));
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        int movement = Mouse.getEventDWheel();
        if (movement < 0) nextPage();
        else if (movement > 0) prevPage();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == Keyboard.KEY_BACK || keyCode == this.mc.gameSettings.keyBindUseItem.getKeyCode())
            this.mc.displayGuiScreen(new GuiCategory(book, category, player, entry));
        if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_RIGHT) && pageNumber + 1 < pageWrapperList.size())
            nextPage();
        if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_LEFT) && pageNumber > 0) prevPage();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) this.mc.displayGuiScreen(new GuiCategory(book, category, player, entry));
        else if (button.id == 1 && pageNumber + 1 < pageWrapperList.size()) nextPage();
        else if (button.id == 2 && pageNumber > 0) prevPage();
        else if (button.id == 3) this.mc.displayGuiScreen(new GuiSearch(book, player, this));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        ResourceLocation key = null;
        for (Map.Entry<ResourceLocation, EntryAbstract> mapEntry : category.entries.entrySet()) if (mapEntry.getValue()
            .equals(entry)) key = mapEntry.getKey();

        if (key != null) OKCore.instance.getPacketHandler()
            .sendToServer(
                new PacketSyncEntry(
                    book.getCategories()
                        .indexOf(category),
                    key,
                    pageNumber));
    }

    public void nextPage() {
        if (pageNumber != pageWrapperList.size() - 1 && !pageWrapperList.isEmpty()) pageNumber++;
    }

    public void prevPage() {
        if (pageNumber != 0) pageNumber--;
    }
}
