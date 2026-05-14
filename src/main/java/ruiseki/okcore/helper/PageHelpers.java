package ruiseki.okcore.helper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.StringEscapeUtils;

import ruiseki.okcore.guide.IPage;
import ruiseki.okcore.guide.gui.GuiBase;
import ruiseki.okcore.guide.pages.PageItemStack;
import ruiseki.okcore.guide.pages.PageText;

public class PageHelpers {

    public static List<String> prepareForLongText(String text, int lineWidth, int firstHeight, int subsequentHeight) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

        text = StringEscapeUtils.unescapeJava(text)
            .replaceAll("\\t", "     ");

        int firstCount = firstHeight / 9;
        int subsequentCount = subsequentHeight / 9;

        List<String> allLines = fontRenderer.listFormattedStringToWidth(text, lineWidth);
        List<String> pages = new ArrayList<>();

        if (allLines.isEmpty()) return pages;

        int linesForFirstPage = Math.min(allLines.size(), firstCount);
        List<String> firstPageList = allLines.subList(0, linesForFirstPage);
        pages.add(combineLines(firstPageList));

        int currentIndex = linesForFirstPage;
        while (currentIndex < allLines.size()) {
            int remaining = allLines.size() - currentIndex;
            int linesForThisPage = Math.min(remaining, subsequentCount);

            List<String> pageLines = allLines.subList(currentIndex, currentIndex + linesForThisPage);
            pages.add(combineLines(pageLines));

            currentIndex += linesForThisPage;
        }

        return pages;
    }

    private static String combineLines(List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            sb.append(lines.get(i));
            if (i < lines.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static List<IPage> pagesForLongText(String text, ItemStack stack) {
        List<String> pageStrings = prepareForLongText(text, 164, 79, 126);
        List<IPage> pageList = new ArrayList<IPage>();

        for (int i = 0; i < pageStrings.size(); i++) {
            if (i == 0) {
                pageList.add(new PageItemStack(pageStrings.get(i), stack));
            } else {
                pageList.add(new PageText(pageStrings.get(i)));
            }
        }
        return pageList;
    }

    public static List<IPage> pagesForLongText(String text) {
        List<IPage> pageList = new ArrayList<IPage>();
        List<String> pageStrings = prepareForLongText(text, 164, 126, 126);
        for (String s : pageStrings) {
            pageList.add(new PageText(s));
        }
        return pageList;
    }

    public static List<IPage> pagesForLongText(String text, Item item) {
        return pagesForLongText(text, new ItemStack(item));
    }

    public static List<IPage> pagesForLongText(String text, Block block) {
        return pagesForLongText(text, new ItemStack(block));
    }

    public static void drawFormattedText(int x, int y, GuiBase guiBase, String toDraw, int color) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        toDraw = StringEscapeUtils.unescapeJava(toDraw)
            .replaceAll("\\t", "     ");
        String[] lines = toDraw.split("\n");
        for (String line : lines) {
            List<String> cutLines = fontRenderer.listFormattedStringToWidth(line, 170);
            for (String cut : cutLines) {
                fontRenderer.drawString(cut, x, y, color, false);
                y += 10;
            }
        }
    }
}
