package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.text.WordUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.pages.PageItemStack;
import ruiseki.okcore.guide.pages.PageText;

public class PageHelpers {

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
        return pagesForLongText(locText, 430);
    }

    public static List<IPage> pagesForLongText(String locText, ItemStack stack) {
        List<IPage> pageList = new ArrayList<IPage>();
        String[] strings = WordUtils.wrap(locText, 240, "/cut", false)
            .split("/cut");
        for (int i = 0; i < strings.length; i++) {
            if (i == 0) pageList.add(new PageItemStack(strings[i], stack));
            else pageList.add(new PageText(strings[i]));
        }
        return pageList;
    }

    @SideOnly(Side.CLIENT)
    public static void drawFormattedText(int x, int y, GuiBase guiBase, String toDraw, int color) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        toDraw = StringEscapeUtils.unescapeJava(toDraw)
            .replaceAll("\\t", "     ");
        String[] lines = toDraw.split("\n");
        for (String line : lines) {
            List<String> cutLines = fontRenderer.listFormattedStringToWidth(line, 164);
            for (String cut : cutLines) {
                fontRenderer.drawString(cut, x, y, color, false);
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
}
