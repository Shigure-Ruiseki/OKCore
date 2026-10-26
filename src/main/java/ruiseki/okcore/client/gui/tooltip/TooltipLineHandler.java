package ruiseki.okcore.client.gui.tooltip;

import java.util.ArrayList;
import java.util.List;

public final class TooltipLineHandler {

    public static final String TOOLTIP_LINESPACE = "§h";
    public static final String TOOLTIP_HANDLER = "§x";

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
        return TOOLTIP_HANDLER + getTipLineId(handler);
    }

    public static ITooltipLine getTipLine(String line) {
        if (line == null || !line.startsWith(TOOLTIP_HANDLER)) {
            return null;
        }

        try {
            int id = Integer.parseInt(line.substring(TOOLTIP_HANDLER.length()));
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
