
package ruiseki.okcore.event.interaction;

/**
 * Represents the outcome of an interaction.
 *
 * <p>
 * Interaction results determine whether an action is consumed,
 * whether a swing animation should occur, and whether statistics
 * should be awarded.
 * </p>
 *
 * <p>
 * These results are interpreted by interaction hooks and are
 * not handled automatically by vanilla interaction methods.
 * </p>
 */
public enum InteractionResult {

    /**
     * The interaction succeeded and consumed the action.
     * A swing animation should occur, and statistics may be awarded.
     */
    SUCCESS,

    /**
     * The interaction succeeded and consumed the action
     * without triggering a swing animation.
     * Statistics may still be awarded.
     */
    CONSUME,

    /**
     * The interaction succeeded and consumed the action
     * without triggering a swing animation or awarding statistics.
     */
    CONSUME_PARTIAL,

    /**
     * The interaction was not handled.
     * The action is not consumed, allowing subsequent interaction
     * handlers or fallback behavior to run.
     */
    PASS,

    /**
     * The interaction failed.
     * The action is not consumed, although callers may explicitly
     * stop further processing when this result is returned.
     */
    FAIL;

    /**
     * Determines whether this result consumes the interaction action.
     *
     * @return {@code true} for {@link #SUCCESS}, {@link #CONSUME},
     *         or {@link #CONSUME_PARTIAL}; otherwise {@code false}.
     */
    public boolean consumesAction() {
        return this == SUCCESS || this == CONSUME || this == CONSUME_PARTIAL;
    }

    /**
     * Determines whether this result requests a swing animation.
     *
     * @return {@code true} only for {@link #SUCCESS}.
     */
    public boolean shouldSwing() {
        return this == SUCCESS;
    }

    /**
     * Determines whether this result permits awarding interaction statistics.
     *
     * @return {@code true} for {@link #SUCCESS} or {@link #CONSUME};
     *         otherwise {@code false}.
     */
    public boolean shouldAwardStats() {
        return this == SUCCESS || this == CONSUME;
    }

    /**
     * Returns a successful interaction result appropriate for
     * the logical side.
     *
     * <p>
     * Client-side success requests a swing animation, while
     * server-side success consumes the action without requesting one.
     * </p>
     *
     * @param isClientSide {@code true} for the logical client,
     *                     {@code false} for the logical server
     * @return {@link #SUCCESS} on the client or {@link #CONSUME}
     *         on the server
     */
    public static InteractionResult sidedSuccess(boolean isClientSide) {
        return isClientSide ? SUCCESS : CONSUME;
    }
}
