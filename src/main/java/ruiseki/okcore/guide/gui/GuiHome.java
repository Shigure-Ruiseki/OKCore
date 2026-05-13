package ruiseki.okcore.guide.gui;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.collect.HashMultimap;

import ruiseki.okcore.OKCore;
import ruiseki.okcore.guide.button.ButtonNext;
import ruiseki.okcore.guide.button.ButtonPrev;
import ruiseki.okcore.guide.button.ButtonSearch;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.wrapper.CategoryWrapper;
import ruiseki.okcore.network.packet.PacketSyncHome;

public class GuiHome extends GuiBase {

    public ResourceLocation outlineTexture;
    public ResourceLocation pageTexture;
    public Book book;
    public HashMultimap<Integer, CategoryWrapper> categoryWrapperMap = HashMultimap.create();
    public ButtonNext buttonNext;
    public ButtonPrev buttonPrev;
    public ButtonSearch buttonSearch;
    public int categoryPage;

    public GuiHome(Book book, EntityPlayer player) {
        super(player);
        this.book = book;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
        this.categoryPage = 0;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.categoryWrapperMap.clear();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        addButton(buttonNext = new ButtonNext(0, guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, this));
        addButton(buttonPrev = new ButtonPrev(1, guiLeft + xSize / 5, guiTop + 5 * ySize / 6, this));
        addButton(buttonSearch = new ButtonSearch(2, (guiLeft + xSize / 6) - 25, guiTop + 5, this));

        int cX = guiLeft + 45;
        int cY = guiTop + 40;
        int drawLoc = 0;
        int i = 0;
        int pageNumber = 0;

        for (CategoryAbstract category : book.getCategories()) {
            if (category.entries.isEmpty()) continue;

            category.onInit(book, this, player);
            switch (drawLoc) {
                case 0: {
                    categoryWrapperMap.put(
                        pageNumber,
                        new CategoryWrapper(
                            book,
                            category,
                            cX,
                            cY,
                            23,
                            23,
                            player,
                            this.fontRendererObj,
                            itemRender,
                            false));
                    cX += 27;
                    drawLoc = 1;
                    break;
                }
                case 1: {
                    categoryWrapperMap.put(
                        pageNumber,
                        new CategoryWrapper(
                            book,
                            category,
                            cX,
                            cY,
                            23,
                            23,
                            player,
                            this.fontRendererObj,
                            itemRender,
                            false));
                    cX += 27;
                    drawLoc = 2;
                    break;
                }
                case 2: {
                    categoryWrapperMap.put(
                        pageNumber,
                        new CategoryWrapper(
                            book,
                            category,
                            cX,
                            cY,
                            23,
                            23,
                            player,
                            this.fontRendererObj,
                            itemRender,
                            false));
                    cX += 27;
                    drawLoc = 3;
                    break;
                }
                case 3: {
                    categoryWrapperMap.put(
                        pageNumber,
                        new CategoryWrapper(
                            book,
                            category,
                            cX,
                            cY,
                            23,
                            23,
                            player,
                            this.fontRendererObj,
                            itemRender,
                            false));
                    drawLoc = 0;
                    cX = guiLeft + 45;
                    cY += 30;
                    break;
                }
            }
            i++;

            if (i >= 16) {
                i = 0;
                cX = guiLeft + 45;
                cY = guiTop + 40;
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
        drawCenteredString(
            fontRendererObj,
            I18n.format(book.getHeader())
                .replace("\\n", "\n")
                .replace("&", "\u00a7"),
            guiLeft + xSize / 2 + 1,
            guiTop + 15,
            0);

        categoryPage = MathHelper.clamp_int(categoryPage, 0, categoryWrapperMap.size() - 1);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(categoryPage))
            if (wrapper.canPlayerSee()) wrapper.draw(mouseX, mouseY, this);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(categoryPage))
            if (wrapper.canPlayerSee()) wrapper.drawExtras(mouseX, mouseY, this);

        drawCenteredString(
            fontRendererObj,
            String.format(
                "%d/%d",
                categoryPage + 1,
                categoryWrapperMap.asMap()
                    .size()),
            guiLeft + xSize / 2,
            guiTop + 5 * ySize / 6,
            0);
        drawCenteredStringWithShadow(
            fontRendererObj,
            I18n.format(book.getTitle()),
            guiLeft + xSize / 2,
            guiTop - 10,
            Color.WHITE.getRGB());

        buttonPrev.visible = categoryPage != 0;
        buttonNext.visible = categoryPage != categoryWrapperMap.asMap()
            .size() - 1 && !categoryWrapperMap.asMap()
                .isEmpty();

        for (GuiButton button : this.buttonList) button.drawButton(this.mc, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int typeofClick) {
        super.mouseClicked(mouseX, mouseY, typeofClick);

        for (CategoryWrapper wrapper : this.categoryWrapperMap.get(categoryPage)) {
            if (wrapper.isMouseOnWrapper(mouseX, mouseY) && wrapper.canPlayerSee()) {
                if (typeofClick == 0) wrapper.category.onLeftClicked(book, mouseX, mouseY, player);

                else if (typeofClick == 1) wrapper.category.onRightClicked(book, mouseX, mouseY, player);
            }
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
        if ((keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_RIGHT)
            && categoryPage + 1 < categoryWrapperMap.asMap()
                .size())
            nextPage();

        if ((keyCode == Keyboard.KEY_DOWN || keyCode == Keyboard.KEY_LEFT) && categoryPage > 0) prevPage();
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0 && categoryPage + 1 < categoryWrapperMap.asMap()
            .size()) nextPage();
        else if (button.id == 1 && categoryPage > 0) prevPage();

        switch (button.id) {
            case 0: {
                if (categoryPage + 1 < categoryWrapperMap.asMap()
                    .size()) nextPage();
                break;
            }
            case 1: {
                if (categoryPage > 0) nextPage();
                break;
            }
            case 2: {
                mc.displayGuiScreen(new GuiSearch(book, player, this));
                break;
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        OKCore.instance.getPacketHandler()
            .sendToServer(new PacketSyncHome(categoryPage));
    }

    public void nextPage() {
        if (categoryPage != categoryWrapperMap.asMap()
            .size() - 1 && !categoryWrapperMap.asMap()
                .isEmpty())
            categoryPage++;
    }

    public void prevPage() {
        if (categoryPage != 0) categoryPage--;
    }
}
