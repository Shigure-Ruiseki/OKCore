package ruiseki.okcore.block.state.property.unlisted;

import java.util.Objects;
import java.util.function.Predicate;

import ruiseki.okcore.block.state.property.IUnlistedProperty;

public abstract class AbstractUnlistedProperty<T extends Comparable<T>> implements IUnlistedProperty<T> {

    private final String name;
    private final Class<T> type;
    private final Predicate<T> validator;
    private final T defaultValue;

    protected AbstractUnlistedProperty(String name, Class<T> type, T defaultValue) {
        this(name, type, value -> true, defaultValue);
    }

    protected AbstractUnlistedProperty(String name, Class<T> type, Predicate<T> validator, T defaultValue) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.validator = Objects.requireNonNull(validator);

        if (defaultValue != null && !isValid(defaultValue)) {
            throw new IllegalArgumentException("Invalid default value: " + defaultValue);
        }

        this.defaultValue = defaultValue;
    }

    @Override
    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public boolean isValid(T value) {
        return value != null && this.type.isInstance(value) && this.validator.test(value);
    }

    @Override
    public String valueToString(T value) {
        return String.valueOf(value);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
