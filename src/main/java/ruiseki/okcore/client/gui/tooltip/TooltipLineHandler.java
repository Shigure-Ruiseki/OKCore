
package ruiseki.okcore.client.gui.tooltip;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry and lookup utility for custom tooltip line renderers.
 *
 * <p>
 * This class allows {@link ITooltipLine} instances to be registered
 * and referenced through specially formatted string markers.
 * These markers can be inserted into ordinary tooltip text lists,
 * allowing a compatible tooltip renderer to resolve and render
 * custom tooltip components instead of plain text.
 * </p>
 *
 * <p>
 * Each registered tooltip line is assigned a numeric identifier
 * corresponding to its index in the internal registry.
 * Identifiers are encoded in markers using the format
 * {@code [[OKCORE:LINE:<id>]]}.
 * </p>
 *
 * <p>
 * Registered handlers remain available until {@link #clear()}
 * is called. Clearing the registry invalidates previously
 * generated identifiers.
 * </p>
 *
 * <p>
 * This registry uses a static list and is not thread-safe.
 * Registration, lookup, and clearing should be performed
 * on the appropriate client rendering thread.
 * </p>
 *
 * @see ITooltipLine
 */
public final class TooltipLineHandler {

    /**
     * Marker representing a custom spacing entry in tooltip text.
     *
     * <p>
     * The interpretation of this marker is determined by the
     * tooltip rendering implementation.
     * </p>
     */
    public static final String TOOLTIP_LINESPACE = "[[OKCORE:SPACE]]";

    /**
     * Prefix used to identify a registered custom tooltip line.
     */
    public static final String TOOLTIP_HANDLER = "[[OKCORE:LINE:";

    /**
     * Suffix terminating a custom tooltip line identifier.
     */
    public static final String TOOLTIP_HANDLER_SUFFIX = "]]";

    private static final List<ITooltipLine> tipLineHandlers = new ArrayList<>();

    /**
     * Prevents instantiation of this utility class.
     */
    private TooltipLineHandler() {}

    /**
     * Registers a custom tooltip line and returns its numeric identifier.
     *
     * <p>
     * Each call appends the supplied handler to the internal registry.
     * Registering the same instance multiple times creates separate
     * entries with different identifiers.
     * </p>
     *
     * <p>
     * Identifiers are assigned sequentially, beginning at zero.
     * They remain valid until the registry is cleared.
     * </p>
     *
     * @param handler the tooltip line handler to register
     * @return the numeric identifier assigned to the handler
     * @throws IllegalArgumentException if {@code handler} is {@code null}
     *
     * @see #register(ITooltipLine)
     */
    public static int getTipLineId(ITooltipLine handler) {
        if (handler == null) {
            throw new IllegalArgumentException("Tooltip line cannot be null");
        }

        tipLineHandlers.add(handler);
        return tipLineHandlers.size() - 1;
    }

    /**
     * Registers a custom tooltip line and returns its string marker.
     *
     * <p>
     * The returned marker can be inserted into a tooltip text list
     * and later resolved using {@link #getTipLine(String)}.
     * </p>
     *
     * <p>
     * For example, registering the first handler produces
     * {@code [[OKCORE:LINE:0]]}.
     * </p>
     *
     * @param handler the tooltip line handler to register
     * @return the formatted marker identifying the registered handler
     * @throws IllegalArgumentException if {@code handler} is {@code null}
     *
     * @see #getTipLineId(ITooltipLine)
     * @see #getTipLine(String)
     */
    public static String register(ITooltipLine handler) {
        return TOOLTIP_HANDLER + getTipLineId(handler) + TOOLTIP_HANDLER_SUFFIX;
    }

    /**
     * Resolves a registered tooltip line from a string containing
     * a custom tooltip marker.
     *
     * <p>
     * The method searches for {@link #TOOLTIP_HANDLER}, extracts
     * the numeric identifier before {@link #TOOLTIP_HANDLER_SUFFIX},
     * and uses it to retrieve the corresponding handler.
     * </p>
     *
     * <p>
     * The marker may appear within a larger string; it does not
     * need to occupy the entire input.
     * </p>
     *
     * <p>
     * Returns {@code null} if the input is {@code null}, the marker
     * is incomplete, the identifier is not a valid integer, or
     * the identifier is outside the registry bounds.
     * </p>
     *
     * @param line the string containing a custom tooltip marker
     * @return the registered tooltip line handler, or {@code null}
     *         if no valid handler can be resolved
     *
     * @see #register(ITooltipLine)
     */
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

    /**
     * Removes all registered custom tooltip line handlers.
     *
     * <p>
     * After clearing the registry, previously generated identifiers
     * are no longer guaranteed to reference their original handlers.
     * New registrations begin again at identifier zero.
     * </p>
     *
     * <p>
     * This method does not automatically remove existing markers
     * from tooltip text lists.
     * </p>
     */
    public static void clear() {
        tipLineHandlers.clear();
    }
}
