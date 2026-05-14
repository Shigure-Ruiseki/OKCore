package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.WordUtils;

import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.impl.Page;
import ruiseki.okcore.guide.pages.PageItemStack;
import ruiseki.okcore.guide.pages.PageText;

public class PageHelpers {

    public static List<IPage> setPagesToUnicode(List<IPage> pages) {
        for (IPage page : pages) if (page instanceof Page) ((Page) page).setUnicodeFlag(true);

        return pages;
    }

    public static List<IPage> pagesForLongText(String locText, int maxLength) {
        List<IPage> pageList = new ArrayList<IPage>();
        for (String s : WordUtils.wrap(locText, maxLength, "/cut", false)
            .split("/cut")) pageList.add(new PageText(s));
        return pageList;
    }

    /**
     * @param locText - Text
     * @return a list of IPages with the text cut to fit on page
     */
    public static List<IPage> pagesForLongText(String locText) {
        return pagesForLongText(locText, 450);
    }

    public static List<IPage> pagesForLongText(String locText, ItemStack stack) {
        List<IPage> pageList = new ArrayList<>();
        String[] strings = WordUtils.wrap(locText, 450, "/cut", false)
            .split("/cut");
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) pageList.add(new PageItemStack(strings[i], stack));
            else pageList.add(new PageText(strings[i]));
        }
        return pageList;
    }

    public static void drawFormattedText(int x, int y, GuiBase guiBase, String toDraw) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        toDraw = StringEscapeUtils.unescapeJava(toDraw)
            .replaceAll("\\t", "     ");
        String[] lines = toDraw.split("\n");
        for (String line : lines) {
            List<String> cutLines = fontRenderer.listFormattedStringToWidth(line, 3 * guiBase.screenWidth / 5);
            for (String cut : cutLines) {
                fontRenderer.drawString(cut, x, y, 0);
                y += 10;
            }
        }
    }

    /**
     * @param locText - Text
     * @param item    - The item to put on the first page
     * @return a list of IPages with the text cut to fit on page
     */
    public static List<IPage> pagesForLongText(String locText, Item item) {
        return pagesForLongText(locText, new ItemStack(item));
    }

    /**
     * @param locText - Text
     * @param block   - The block to put on the first page
     * @return a list of IPages with the text cut to fit on page
     */
    public static List<IPage> pagesForLongText(String locText, Block block) {
        return pagesForLongText(locText, new ItemStack(block));
    }

    /**
     * @param recipe1 - The first IRecipe to compare
     * @param recipe2 - The second IRecipe to compare
     * @return whether or not the class, size and the output of the recipes are the same
     */
    public static boolean areIRecipesEqual(IRecipe recipe1, IRecipe recipe2) {
        if (recipe1 == recipe2) return true;
        if (recipe1 == null || recipe2 == null || recipe1.getClass() != recipe2.getClass()) return false;
        if (recipe1.equals(recipe2)) return true;
        if (!recipe1.getRecipeOutput()
            .isItemEqual(recipe2.getRecipeOutput())) return false;
        // if (recipe1.getRecipeSize() != recipe2.getRecipeSize()) return false;//FN was removed, there is no size now
        return true;
    }
}
