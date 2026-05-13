package ruiseki.okcore.guide.gui;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import ruiseki.okcore.guide.button.ButtonBack;
import ruiseki.okcore.guide.button.ButtonNext;
import ruiseki.okcore.guide.button.ButtonPrev;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class GuiSearch extends GuiBase {

    private Book book;
    private ResourceLocation outlineTexture;
    private ResourceLocation pageTexture;
    private ButtonNext buttonNext;
    private ButtonPrev buttonPrev;
    private GuiTextField searchField;
    private GuiScreen parent;
    private List<List<Pair<EntryAbstract, CategoryAbstract>>> searchResults;
    private int currentPage = 0;
    private String lastQuery = "";

    public GuiSearch(Book book, EntityPlayer player, GuiScreen parent) {
        super(player);

        this.book = book;
        this.pageTexture = book.getPageTexture();
        this.outlineTexture = book.getOutlineTexture();
        this.parent = parent;
        this.searchResults = getMatches(book, null, player);
    }

    @Override
    public void initGui() {
        buttonList.clear();

        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;

        addButton(new ButtonBack(0, guiLeft + xSize / 6, guiTop, this));
        addButton(buttonNext = new ButtonNext(1, guiLeft + 4 * xSize / 6, guiTop + 5 * ySize / 6, this));
        addButton(buttonPrev = new ButtonPrev(2, guiLeft + xSize / 5, guiTop + 5 * ySize / 6, this));

        searchField = new GuiTextField(fontRendererObj, guiLeft + 43, guiTop + 12, 100, 10);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setFocused(true);
        searchResults = getMatches(book, null, player);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        mc.getTextureManager()
            .bindTexture(pageTexture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        mc.getTextureManager()
            .bindTexture(outlineTexture);
        drawTexturedModalRectWithColor(guiLeft, guiTop, 0, 0, xSize, ySize, book.getColor());

        drawRect(
            searchField.xPosition - 1,
            searchField.yPosition - 1,
            searchField.xPosition + searchField.width + 1,
            searchField.yPosition + searchField.height + 1,
            new Color(166, 166, 166, 128).getRGB());
        drawRect(
            searchField.xPosition,
            searchField.yPosition,
            searchField.xPosition + searchField.width,
            searchField.yPosition + searchField.height,
            new Color(58, 58, 58, 128).getRGB());
        searchField.drawTextBox();

        int entryX = guiLeft + 37;
        int entryY = guiTop + 30;

        if (searchResults.size() != 0 && currentPage >= 0 && currentPage < searchResults.size()) {
            List<Pair<EntryAbstract, CategoryAbstract>> pageResults = searchResults.get(currentPage);
            for (Pair<EntryAbstract, CategoryAbstract> entry : pageResults) {
                entry.getLeft()
                    .draw(
                        book,
                        entry.getRight(),
                        entryX,
                        entryY,
                        4 * xSize / 6,
                        10,
                        mouseX,
                        mouseY,
                        this,
                        fontRendererObj);
                entry.getLeft()
                    .drawExtras(
                        book,
                        entry.getRight(),
                        entryX,
                        entryY,
                        4 * xSize / 6,
                        10,
                        mouseX,
                        mouseY,
                        this,
                        fontRendererObj);

                if (GuiHelpers.isMouseBetween(mouseX, mouseY, entryX, entryY, 4 * xSize / 6, 10)) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) this.drawHoveringText(
                        Lists.newArrayList(
                            entry.getRight()
                                .getLocalizedName()),
                        width,
                        height,
                        fontRendererObj);

                    if (Mouse.isButtonDown(0)) {
                        GuiHelpers.openBookClient(book, entry.getRight(), entry.getLeft(), player);
                        return;
                    }
                }

                entryY += 13;
            }
        }

        buttonPrev.visible = currentPage != 0;
        buttonNext.visible = currentPage != searchResults.size() - 1 && !searchResults.isEmpty();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1) {
            if (GuiHelpers.isMouseBetween(
                mouseX,
                mouseY,
                searchField.xPosition,
                searchField.yPosition,
                searchField.width,
                searchField.height)) {
                searchField.setText("");
                lastQuery = "";
                searchResults = getMatches(book, "", player);
                return;
            } else mc.displayGuiScreen(parent);
        }

        searchField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        int movement = Mouse.getEventDWheel();
        if (movement < 0 && buttonNext.visible && currentPage <= searchResults.size()) currentPage++;
        else if (movement > 0 && buttonPrev.visible && currentPage > 0) currentPage--;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!searchField.isFocused()) super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_ESCAPE) searchField.setFocused(false);

        searchField.textboxKeyTyped(typedChar, keyCode);
        if (!searchField.getText()
            .equalsIgnoreCase(lastQuery)) {
            lastQuery = searchField.getText();
            searchResults = getMatches(book, searchField.getText(), player);
            if (currentPage > searchResults.size()) currentPage = searchResults.size() - 1;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                mc.displayGuiScreen(parent);
                break;
            }
            case 1: {
                if (currentPage <= searchResults.size() - 1) currentPage++;
                break;
            }
            case 2: {
                if (currentPage > 0) currentPage--;
                break;
            }
        }
    }

    @NotNull
    static List<List<Pair<EntryAbstract, CategoryAbstract>>> getMatches(Book book, @Nullable String query,
        EntityPlayer player) {
        List<Pair<EntryAbstract, CategoryAbstract>> discovered = Lists.newArrayList();

        for (CategoryAbstract category : book.getCategories()) {
            if (!category.canSee(player)) continue;

            for (EntryAbstract entry : category.entries.values()) {
                if (!entry.canSee(player)) continue;

                if (Strings.isNullOrEmpty(query) || entry.getLocalizedName()
                    .toLowerCase(Locale.ENGLISH)
                    .contains(query.toLowerCase(Locale.ENGLISH))) discovered.add(Pair.of(entry, category));
            }
        }

        return Lists.partition(discovered, 10);
    }
}
