package ruiseki.okcore.block.state.property.listed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import ruiseki.okcore.block.state.property.IListedProperty;

public abstract class AbstractListedProperty<T extends Comparable<T>> implements IListedProperty<T> {

    private final String name;
    private final Class<T> type;
    private final List<T> allowedValues;
    private final Map<String, T> valuesByName;

    protected AbstractListedProperty(String name, Class<T> type, Collection<T> allowedValues,
        Function<T, String> nameProvider) {
        this.name = Objects.requireNonNull(name, "name");
        this.type = Objects.requireNonNull(type, "type");

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Property name cannot be empty");
        }

        Map<String, T> values = new LinkedHashMap<>();

        for (T value : allowedValues) {
            Objects.requireNonNull(value, "Allowed value");

            if (!type.isInstance(value)) {
                throw new IllegalArgumentException("Invalid value type");
            }

            String valueName = Objects.requireNonNull(nameProvider.apply(value), "Serialized value name");

            if (valueName.isEmpty()) {
                throw new IllegalArgumentException("Value name cannot be empty");
            }

            if (values.putIfAbsent(valueName, value) != null) {
                throw new IllegalArgumentException("Duplicate serialized value: " + valueName);
            }
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("Property must have at least one allowed value");
        }

        this.valuesByName = Collections.unmodifiableMap(values);
        this.allowedValues = Collections.unmodifiableList(new ArrayList<>(values.values()));
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
        return this.allowedValues.contains(value);
    }

    @Override
    public String valueToString(T value) {
        return this.getName(value);
    }

    @Override
    public Collection<T> getAllowedValues() {
        return this.allowedValues;
    }

    @Override
    public Optional<T> parseValue(String value) {
        return Optional.ofNullable(this.valuesByName.get(value));
    }

    @Override
    public String getName(T value) {
        if (!this.isValid(value)) {
            throw new IllegalArgumentException("Invalid value '" + value + "' for property '" + this.name + "'");
        }

        for (Map.Entry<String, T> entry : this.valuesByName.entrySet()) {
            if (entry.getValue()
                .equals(value)) {
                return entry.getKey();
            }
        }

        throw new IllegalStateException("Value mapping is inconsistent");
    }

    @Override
    public String toString() {
        return this.name;
    }
}
