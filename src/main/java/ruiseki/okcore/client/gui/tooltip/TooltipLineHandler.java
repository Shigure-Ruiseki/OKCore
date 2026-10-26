package ruiseki.okcore.client.gui.tooltip;

import java.util.ArrayList;
import java.util.List;

public final class TooltipLineHandler {

    public static final String TOOLTIP_LINESPACE = "[[OKCORE:SPACE]]";
    public static final String TOOLTIP_HANDLER = "[[OKCORE:LINE:";
    public static final String TOOLTIP_HANDLER_SUFFIX = "]]";

    private static final List<ITooltipLine> tipLineHandlers = new ArrayList<>();

    private TooltipLineHandler() {}

    public static int getTipLineId(ITooltipLine handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Tooltip line cannot be null");
        }

        tipLineHandlers.add(handler);
        return tipLineHandlers.size() - 1;
    }

    public static String register(ITooltipLine handler) {
        return TOOLTIP_HANDLER + getTipLineId(handler) + TOOLTIP_HANDLER_SUFFIX;
    }

    public static ITooltipLine getTipLine(String line) {
        if (line == null) {
            return null;
        }

        int start = line.indexOf(TOOLTIP_HANDLER);
        if (start < 0) {
            return null;
        }

        start += TOOLTIP_HANDLER.length();
        int end = line.indexOf(TOOLTIP_HANDLER_SUFFIX, start);
        if (end < 0) {
            return null;
        }

        try {
            int id = Integer.parseInt(line.substring(start, end));
            if (id < 0 || id >= tipLineHandlers.size()) {
                return null;
            }
            return tipLineHandlers.get(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void clear() {
        tipLineHandlers.clear();
    }
}
