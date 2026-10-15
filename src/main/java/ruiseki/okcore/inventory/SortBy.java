package ruiseki.okcore.inventory;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import ruiseki.okcore.datastructure.StringRepresentable;

public enum SortBy implements StringRepresentable {

    NAME,
    MOD_ID,
    COUNT,
    ORE_DICT,
    TAG;

    public SortBy next(List<SortBy> allowed) {
        if (allowed == null || allowed.isEmpty()) {
            return this;
        }

        int index = allowed.indexOf(this);
        return allowed.get(index < 0 ? 0 : (index + 1) % allowed.size());
    }

    public SortBy next() {
        return VALUES[(ordinal() + 1) % VALUES.length];
    }

    public SortBy prev(List<SortBy> allowed) {
        if (allowed == null || allowed.isEmpty()) {
            return this;
        }

        int index = allowed.indexOf(this);
        return allowed.get(index < 0 ? 0 : Math.floorMod(index - 1, allowed.size()));
    }

    public SortBy prev() {
        return VALUES[Math.floorMod(ordinal() - 1, VALUES.length)];
    }

    private static final Map<String, SortBy> NAME_VALUES;
    private static final SortBy[] VALUES;

    static {
        ImmutableMap.Builder<String, SortBy> builder = new ImmutableMap.Builder<>();
        for (SortBy value : SortBy.values()) {
            builder.put(value.getSerializedName(), value);
        }
        NAME_VALUES = builder.build();
        VALUES = values();
    }

    public static SortBy fromName(String name) {
        return NAME_VALUES.getOrDefault(name, NAME);
    }

    public static SortBy fromIndex(int index) {
        return index >= 0 && index < VALUES.length ? VALUES[index] : NAME;
    }

    @Override
    public String getSerializedName() {
        return name();
    }
}
