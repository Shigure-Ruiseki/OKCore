package ruiseki.okcore.block.state.property.unlisted;

import java.util.Optional;

public abstract class IntegerUnlistedProperty extends AbstractUnlistedProperty<Integer> {

    private final int min;
    private final int max;

    protected IntegerUnlistedProperty(String name) {
        this(name, 0);
    }

    protected IntegerUnlistedProperty(String name, int defaultValue) {
        this(name, Integer.MIN_VALUE, Integer.MAX_VALUE, defaultValue);
    }

    protected IntegerUnlistedProperty(String name, int min, int max) {
        this(name, min, max, 0);
    }

    protected IntegerUnlistedProperty(String name, int min, int max, int defaultValue) {
        super(name, Integer.class, value -> value >= min && value <= max, defaultValue);

        if (min > max) {
            throw new IllegalArgumentException("min cannot be greater than max");
        }

        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }

    @Override
    public Optional<Integer> parseValue(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return this.isValid(parsed) ? Optional.of(parsed) : Optional.empty();
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
