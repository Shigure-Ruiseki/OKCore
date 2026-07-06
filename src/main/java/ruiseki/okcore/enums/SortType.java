package ruiseki.okcore.enums;

public enum SortType implements ICyclicEnum<SortType> {

    BY_NAME,
    BY_MOD_ID,
    BY_COUNT,
    BY_ORE_DICT;

    public static SortType byIndex(int index) {
        SortType[] values = SortType.values();
        if (index < 0 || index >= values.length) return BY_NAME;
        return values[index];
    }

    public static SortType byName(String name) {
        if (name == null || name.isEmpty()) return BY_NAME;
        for (SortType type : SortType.values()) {
            if (type.name()
                .equalsIgnoreCase(name)) {
                return type;
            }
        }
        return BY_NAME;
    }
}
