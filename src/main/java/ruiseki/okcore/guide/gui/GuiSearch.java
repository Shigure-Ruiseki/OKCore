package ruiseki.okcore.guide.gui;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;
import ruiseki.okcore.helper.GuiHelpers;

public class GuiSearch extends GuiBase {

    private final int renderXOffset = 0;
    private final int renderYOffset = 13;
    private GuiScreen parent;
    private GuiTextField searchField;
    private List<List<Pair<EntryAbstract, CategoryAbstract>>> searchResults;
    private String lastQuery = "";

    public GuiSearch(Book book, EntityPlayer player, GuiScreen parent) {
        super(book, player);
        this.parent = parent;
        this.searchResults = getMatches(book, null, player);
    }

    @Override
    public void initGui() {
        super.initGui();
        searchField = new GuiTextField(fontRendererObj, screenLeft() + 43, screenTop() + 12, 100, 10);
        searchField.setEnableBackgroundDrawing(false);
        searchField.setFocused(true);
        searchResults = getMatches(book, null, player);

        addButtons(true, false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

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

        int entryX = pageLeft() + renderXOffset;
        int entryY = pageTop() + renderYOffset;

        if (!searchResults.isEmpty() && currentPage() >= 0 && currentPage() < searchResults.size()) {
            List<Pair<EntryAbstract, CategoryAbstract>> pageResults = searchResults.get(currentPage());
            for (Pair<EntryAbstract, CategoryAbstract> entry : pageResults) {
                entry.getLeft()
                    .draw(
                        book,
                        entry.getRight(),
                        entryX,
                        entryY,
                        pageWidth(),
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
                        pageWidth(),
                        10,
                        mouseX,
                        mouseY,
                        this,
                        fontRendererObj);

                if (GuiHelpers.isMouseBetween(mouseX, mouseY, entryX, entryY, pageWidth(), 10)) {
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
    }

    @Override
    protected void goBack() {
        mc.displayGuiScreen(parent);
    }

    @Override
    protected void startSearch() {

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
    public void keyTyped(char typedChar, int keyCode) {
        if (!searchField.isFocused()) super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_ESCAPE) searchField.setFocused(false);

        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
            updateSearch();
        }
    }

    private void updateSearch() {
        if (!searchField.getText()
            .equalsIgnoreCase(lastQuery)) {
            lastQuery = searchField.getText();
            searchResults = getMatches(book, searchField.getText(), player());
            if (currentPage() > searchResults.size()) setPage(searchResults.size() - 1);
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
                nextPage();
                break;
            }
            case 2: {
                prevPage();
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

    @Override
    public int getPageCount() {
        return searchResults.size();
    }
}
