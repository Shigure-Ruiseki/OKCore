package ruiseki.okcore.helper;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

import ruiseki.okcore.OKCore;

/**
 * A collection of String helper methods.
 *
 * @author rubensworks
 *
 */
public class StringHelpers {

    /**
     * One day i might make this a setting or an input arg for now i have no use to turn it off
     */
    public static final boolean matchWildcard = true;

    private static final String SPACE = " ";
    private static final String NEWLINE_PATTERN = "\\\\n|\\r\\n|\\r|\\n";

    /**
     * Splits the input string into lines while preserving complete words.
     *
     * <p>
     * Explicit newline characters and literal {@code \n} sequences
     * are treated as line breaks.
     * </p>
     *
     * <p>
     * Words are kept intact, even when an individual word exceeds
     * the specified maximum length.
     * </p>
     *
     * @param input     the input text
     * @param maxLength the maximum number of characters per line
     * @param prefix    a prefix added to each resulting line
     * @return the resulting lines
     */
    public static List<String> splitLines(String input, int maxLength, String prefix) {
        List<String> lines = Lists.newArrayList();

        if (input == null) {
            return lines;
        }

        maxLength = Math.max(1, maxLength);
        prefix = prefix == null ? "" : prefix;

        for (String paragraph : input.split(NEWLINE_PATTERN, -1)) {
            if (paragraph.trim()
                .isEmpty()) {
                lines.add(prefix);
                continue;
            }

            StringBuilder buffer = new StringBuilder();

            for (String word : paragraph.trim()
                .split("\\s+")) {
                int requiredLength = buffer.isEmpty() ? word.length() : buffer.length() + 1 + word.length();

                if (!buffer.isEmpty() && requiredLength > maxLength) {
                    lines.add(prefix + buffer);
                    buffer.setLength(0);
                }

                if (!buffer.isEmpty()) {
                    buffer.append(SPACE);
                }

                buffer.append(word);
            }

            if (!buffer.isEmpty()) {
                lines.add(prefix + buffer);
            }
        }

        return lines;
    }

    /**
     * Splits the input string into lines based on the maximum pixel width.
     *
     * <p>
     * Text is wrapped at word boundaries using the specified font renderer.
     * Explicit newline characters and literal {@code \n} sequences
     * are also preserved.
     * </p>
     *
     * <p>
     * Minecraft formatting codes are handled by {@link FontRenderer},
     * allowing formatted text to be wrapped without measuring raw
     * character lengths.
     * </p>
     *
     * @param input    the input text
     * @param font     the font renderer used to measure and wrap text
     * @param maxWidth the maximum width of each line in pixels
     * @param prefix   a prefix added to each resulting line
     * @return the wrapped lines
     */
    public static List<String> splitLines(String input, FontRenderer font, int maxWidth, String prefix) {
        List<String> lines = Lists.newArrayList();

        if (input == null || font == null || maxWidth <= 0) {
            return lines;
        }

        prefix = prefix == null ? "" : prefix;

        int availableWidth = Math.max(1, maxWidth - font.getStringWidth(prefix));

        for (String paragraph : input.split(NEWLINE_PATTERN, -1)) {
            if (paragraph.isEmpty()) {
                lines.add(prefix);
                continue;
            }

            List<String> wrappedLines = font.listFormattedStringToWidth(paragraph, availableWidth);

            if (wrappedLines.isEmpty()) {
                lines.add(prefix);
                continue;
            }

            for (String line : wrappedLines) {
                lines.add(prefix + line);
            }
        }

        return lines;
    }

    /**
     * Splits the input string into lines based on the maximum pixel width.
     *
     * @param input    the input text
     * @param font     the font renderer used to measure and wrap text
     * @param maxWidth the maximum width of each line in pixels
     * @return the wrapped lines
     */
    public static List<String> splitLines(String input, FontRenderer font, int maxWidth) {
        return splitLines(input, font, maxWidth, "");
    }

    public static String getFormattedString(String string, ChatFormatting formatting) {
        return formatting + string + ChatFormatting.RESET;
    }

    public static String getFormattedString(ChatFormatting pre, String string, ChatFormatting post) {
        return ChatFormatting.RESET + "" + pre + string + ChatFormatting.RESET + post;
    }

    public static ChatFormatting getValidFormatting(String formatting) {
        ChatFormatting result = ChatFormatting.getByName(formatting);
        return (result != null) ? result : ChatFormatting.WHITE;
    }

    public static String getDashedLine(int length) {
        return StringUtils.repeat('-', length);
    }

    public static String pad(String original, int targetLength) {
        int padLength = targetLength - original.length();
        if (padLength <= 0) return original;

        int leftPad = padLength / 2;
        String leftPadded = StringUtils.leftPad(original, original.length() + leftPad);
        return StringUtils.rightPad(leftPadded, targetLength);
    }

    public static String uppercaseFirst(String original) {
        return original.substring(0, 1)
            .toUpperCase() + original.substring(1);
    }

    /**
     * If the list has "hc:*_sapling" and input is "hc:whatever_sapling" then match is true
     *
     * @param list
     * @param toMatch
     * @return
     */
    public static boolean isInList(final List<String> list, ResourceLocation toMatch) {
        if (toMatch == null || list == null) {
            return false;
        }
        String id = toMatch.getResourceDomain();
        for (String strFromList : list) {
            if (strFromList == null || strFromList.isEmpty()) {
                continue;// just ignore me
            }
            if (strFromList.equals(id)) {
                return true;
            }
            if (matchWildcard) {
                String[] blockIdArray = strFromList.split(":");
                if (blockIdArray.length <= 1) {
                    OKCore.okLog(Level.ERROR, "Invalid config value for block : " + strFromList);
                    return false;
                }
                String modIdFromList = blockIdArray[0];
                String blockIdFromList = blockIdArray[1];// has the *
                String modIdToMatch = toMatch.getResourceDomain();
                String blockIdToMatch = toMatch.getResourcePath();
                if (!modIdFromList.equals(modIdToMatch)) {
                    continue;
                }
                String blockIdListWC = blockIdFromList.replace("*", "");
                if (blockIdToMatch.contains(blockIdListWC)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static int offsetByCodepoints(String text, int index, int offset) {
        int i = text.length();
        if (offset >= 0) {
            for (int j = 0; index < i && j < offset; ++j) {
                if (Character.isHighSurrogate(text.charAt(index++)) && index < i
                    && Character.isLowSurrogate(text.charAt(index))) {
                    ++index;
                }
            }
        } else {
            for (int k = offset; index > 0 && k < 0; ++k) {
                --index;
                if (Character.isLowSurrogate(text.charAt(index)) && index > 0
                    && Character.isHighSurrogate(text.charAt(index - 1))) {
                    --index;
                }
            }
        }

        return index;
    }
}
