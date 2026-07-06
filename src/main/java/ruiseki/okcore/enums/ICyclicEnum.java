package ruiseki.okcore.enums;

import java.util.List;

public interface ICyclicEnum<E extends Enum<E> & ICyclicEnum<E>> {

    @SuppressWarnings("unchecked")
    default E[] getEnumValues() {
        return ((Class<E>) this.getClass()).getEnumConstants();
    }

    default int getIndex() {
        return this.getOrdinal();
    }

    default int getOrdinal() {
        return ((Enum<?>) this).ordinal();
    }

    default E next() {
        E[] values = getEnumValues();
        return values[(getOrdinal() + 1) % values.length];
    }

    default E prev() {
        E[] values = getEnumValues();
        return values[Math.floorMod(getOrdinal() - 1, values.length)];
    }

    @SuppressWarnings("unchecked")
    default E next(List<E> allowed) {
        if (allowed == null || allowed.isEmpty()) return (E) this;
        int i = allowed.indexOf((E) this);
        if (i < 0) return allowed.getFirst();
        return allowed.get((i + 1) % allowed.size());
    }

    @SuppressWarnings("unchecked")
    default E prev(List<E> allowed) {
        if (allowed == null || allowed.isEmpty()) return (E) this;
        int i = allowed.indexOf((E) this);
        if (i < 0) return allowed.getFirst();
        return allowed.get(Math.floorMod(i - 1, allowed.size()));
    }
}
