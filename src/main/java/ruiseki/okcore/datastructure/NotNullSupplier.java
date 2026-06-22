package ruiseki.okcore.datastructure;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

/**
 * Equivalent to {@link Supplier}, except with nonnull contract.
 *
 * @see Supplier
 */
@FunctionalInterface
public interface NotNullSupplier<T> {

    @NotNull
    T get();
}
