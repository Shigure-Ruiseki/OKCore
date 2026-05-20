package ruiseki.okcore.helper;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Helpers {

    public static <T> Stream<T> toStream(Optional<? extends T> optional) {
        return orElseGet(optional.map(Stream::of), Stream::empty);
    }

    public static <U> U orElseGet(final Optional<? extends U> optional, final Supplier<? extends U> other) {
        if (optional.isPresent()) {
            return optional.get();
        }
        return other.get();
    }
}
