package ruiseki.okcore.datastructure;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

/**
 * Equivalent to {@link Consumer}, except with nonnull contract.
 *
 * @see Consumer
 */
@FunctionalInterface
public interface NotNullConsumer<T> {

    void accept(@NotNull T t);
}
