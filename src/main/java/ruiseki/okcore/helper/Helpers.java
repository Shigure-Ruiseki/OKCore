package ruiseki.okcore.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

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

    public static <K, V> Map<K, Set<V>> copyToMSImmutable(Map<K, Set<V>> map) {
        if (map == null) return ImmutableMap.of();
        Map<K, Set<V>> copy = new HashMap<>();
        for (Map.Entry<K, Set<V>> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                copy.put(entry.getKey(), ImmutableSet.copyOf(entry.getValue()));
            }
        }
        return ImmutableMap.copyOf(copy);
    }

    public static <K1, K2, V> Map<K1, Map<K2, V>> copyMMToImmutable(Map<K1, Map<K2, V>> map) {
        if (map == null) return ImmutableMap.of();

        Map<K1, Map<K2, V>> copy = new HashMap<>();
        for (Map.Entry<K1, Map<K2, V>> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                copy.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
            }
        }
        return ImmutableMap.copyOf(copy);
    }
}
