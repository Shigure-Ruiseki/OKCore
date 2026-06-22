package ruiseki.okcore.datastructure;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

/**
 * Equivalent to {@link Predicate}, except with nonnull contract.
 *
 * @see Predicate
 */
@FunctionalInterface
public interface NotNullPredicate<T> {

    boolean test(@NotNull T t);
}
