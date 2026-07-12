package ruiseki.okcore.datastructure;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

/**
 * Proxy object for a value that is calculated on first access.
 * Same as {@link Lazy}, but with a nonnull contract.
 * 
 * @param <T> The type of the value
 */
public interface NotNullLazy<T> extends NotNullSupplier<T> {

    /**
     * Constructs a lazy-initialized object
     *
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> NotNullLazy<T> of(@NotNull NotNullSupplier<T> supplier) {
        Lazy<T> lazy = Lazy.of(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }

    /**
     * Constructs a thread-safe lazy-initialized object
     *
     * @param supplier The supplier for the value, to be called the first time the value is needed.
     */
    static <T> NotNullLazy<T> concurrentOf(@NotNull NotNullSupplier<T> supplier) {
        Lazy<T> lazy = Lazy.concurrentOf(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }
}
