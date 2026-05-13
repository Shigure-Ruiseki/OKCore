package ruiseki.okcore.guide.gui;

import java.awt.Color;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.button.ButtonBack;
import ruiseki.okcore.guide.button.ButtonNext;
import ruiseki.okcore.guide.button.ButtonPrev;
import ruiseki.okcore.guide.button.ButtonSearch;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.guide.wrapper.EntryWrapper;
import ruiseki.okcore.network.packet.PacketSyncCategory;

public class GuiCategory extends GuiBase {

    public ResourceLocation outlineTexture;
    public ResourceLocation pageTexture;
    public Book book;
    public CategoryAbstract category;
    public HashMultimap<Integer, EntryWrapper> entryWrapperMap = HashMultimap.create();
    public ButtonBack buttonBack;
    public ButtonNext buttonNext;
    public ButtonPrev buttonPrev;
    public ButtonSearch buttonSearch;
    public int entryPage;
    @Nullable
    public EntryAbstract startEntry;

    public GuiCategory(Book book, CategoryAbstract category, EntityPlayer player, @Nullable EntryAbstract startEntry) {
        super(player);
        this.book = book;
        this.category = category;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
        this.entryPage = 0;
        this.startEntry = startEntry;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.entryWrapperMap.clear();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        this.buttonList.add(buttonBack = new ButtonBack(0, guiLeft + xSize / 6, guiTop, this));
        this.buttonList.add(buttonNext = new ButtonNext(1, guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, this));
        this.buttonList.add(buttonPrev = new ButtonPrev(2, guiLeft + xSize / 5, guiTop + 5 * ySize / 6, this));
        this.buttonList.add(buttonSearch = new ButtonSearch(3, (guiLeft + xSize / 6) - 25, guiTop + 5, this));

        int eX = guiLeft + 37;
        int eY = guiTop + 15;
        int i = 0;
        int pageNumber = 0;
        List<EntryAbstract> entries = Lists.newArrayList(category.entries.values());
        for (EntryAbstract entry : entries) {
            entry.onInit(book, category, this, player);
            entryWrapperMap.put(
                pageNumber,
                new EntryWrapper(this, book, category, entry, eX, eY, 4 * xSize / 6, 10, player, this.fontRendererObj));
            if (entry.equals(this.startEntry)) {
                this.startEntry = null;
                this.entryPage = pageNumber;
            }
            eY += 13;
            i++;

            if (i >= 11) {
                i = 0;
                eY = guiTop + 15;
                pageNumber++;
            }
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

        entryPage = MathHelper.clamp_int(entryPage, 0, entryWrapperMap.size() - 1);

        for (EntryWrapper wrapper : this.entryWrapperMap.get(entryPage)) {
            if (wrapper.canPlayerSee()) {
                wrapper.draw(mouseX, mouseY, this);
                wrapper.drawExtras(mouseX, mouseY, this);
            }
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                wrapper.onHoverOver(mouseX, mouseY);
            }
        }

        drawCenteredString(
            fontRendererObj,
            String.format(
                "%d/%d",
                entryPage + 1,
                entryWrapperMap.asMap()
                    .size()),
            guiLeft + xSize / 2,
            guiTop + 5 * ySize / 6,
            0);
        drawCenteredStringWithShadow(
            fontRendererObj,
            category.getLocalizedName(),
            guiLeft + xSize / 2,
            guiTop - 10,
            Color.WHITE.getRGB());

        buttonPrev.visible = entryPage != 0;
        buttonNext.visible = entryPage != entryWrapperMap.asMap()
            .size() - 1 && !entryWrapperMap.asMap()
                .isEmpty();

        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int typeofClick) {
        super.mouseClicked(mouseX, mouseY, typeofClick);

        for (EntryWrapper wrapper : this.entryWrapperMap.get(entryPage)) {
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                if (typeofClick == 0) wrapper.entry.onLeftClicked(book, category, mouseX, mouseY, player, this);
                else if (typeofClick == 1) wrapper.entry.onRightClicked(book, category, mouseX, mouseY, player, this);
            }
        }

        if (typeofClick == 1) this.mc.displayGuiScreen(new GuiHome(book, player));
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
            this.mc.displayGuiScreen(new GuiHome(book, player));
        if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_RIGHT) && entryPage + 1 < entryWrapperMap.asMap()
            .size()) nextPage();
        if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_LEFT) && entryPage > 0) prevPage();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) this.mc.displayGuiScreen(new GuiHome(book, player));
        else if (button.id == 1 && entryPage + 1 < entryWrapperMap.asMap()
            .size()) nextPage();
        else if (button.id == 2 && entryPage > 0) prevPage();
        else if (button.id == 3) this.mc.displayGuiScreen(new GuiSearch(book, player, this));
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        OKCore.instance.getPacketHandler()
            .sendToServer(
                new PacketSyncCategory(
                    book.getCategories()
                        .indexOf(category),
                    entryPage));
    }

    public void nextPage() {
        if (entryPage != entryWrapperMap.asMap()
            .size() - 1) entryPage++;
    }

    public void prevPage() {
        if (entryPage != 0) entryPage--;
    }
}
