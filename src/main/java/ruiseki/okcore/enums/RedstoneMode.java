package ruiseki.okcore.enums;

public enum RedstoneMode implements ICyclicEnum<RedstoneMode> {

    ALWAYS_ON,
    HIGH_ON,
    HIGH_OFF,
    ALWAYS_OFF;

    public static boolean isActive(RedstoneMode mode, boolean redstonePowered) {
        return (mode == ALWAYS_ON) || (mode == HIGH_ON && redstonePowered) || (mode == HIGH_OFF && !redstonePowered);
    }

    public int getIndex() {
        return this.ordinal();
    }

    public static RedstoneMode byIndex(int index) {
        RedstoneMode[] values = RedstoneMode.values();
        if (index < 0 || index >= values.length) return ALWAYS_ON;
        return values[index];
    }

    public static RedstoneMode byName(String name) {
        if (name == null || name.isEmpty()) return ALWAYS_ON;
        for (RedstoneMode type : RedstoneMode.values()) {
            if (type.name()
                .equalsIgnoreCase(name)) {
                return type;
            }
        }
        return ALWAYS_ON;
    }
}
