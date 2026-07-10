package ruiseki.okcore.helper;

import java.util.List;

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
    private static final String NEWLINE_PATTERN = "\\\\n";

    /**
     * Split the input string into lines while preserving the full words.
     * This will also forcefully add newlines when '\n' is found in the input string.
     *
     * @param input     The input sentence.
     * @param maxLength The maximum length of a line.
     * @param prefix    A prefix to add to each produced line. This will not increase the character
     *                  count per line.
     * @return The sentence split into lines.
     */
    public static List<String> splitLines(String input, int maxLength, String prefix) {
        List<String> list = Lists.newLinkedList();

        for (String partialInput : input.split(NEWLINE_PATTERN)) {
            StringBuilder buffer = new StringBuilder();
            for (String word : partialInput.split(SPACE)) {
                if (!buffer.isEmpty()) {
                    buffer.append(SPACE);
                }
                buffer.append(word);
                if (buffer.length() >= maxLength) {
                    list.add(prefix + buffer.toString());
                    buffer = new StringBuilder();
                }
            }
            if (!buffer.isEmpty()) {
                list.add(prefix + buffer.toString());
            }
        }

        return list;
    }

    public static String getFormattedString(String string, ChatFormatting formatting) {
        return formatting + string + ChatFormatting.RESET;
    }

    public static String getFormattedString(ChatFormatting pre, String string, ChatFormatting post) {
        return ChatFormatting.RESET + "" + pre + string + ChatFormatting.RESET + "" + post;
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
}
