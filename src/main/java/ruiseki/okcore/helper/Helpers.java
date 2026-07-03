package ruiseki.okcore.helper;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import net.minecraft.util.ResourceLocation;

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

    public static ResourceLocation parseLocation(String location) {
        int idx = location.indexOf(':');
        if (idx == -1) {
            return new ResourceLocation("minecraft", location);
        }
        return new ResourceLocation(location.substring(0, idx), location.substring(idx + 1));
    }
}
