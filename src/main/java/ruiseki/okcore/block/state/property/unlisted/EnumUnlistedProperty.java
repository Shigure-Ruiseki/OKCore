package ruiseki.okcore.block.state.property.unlisted;

import java.util.Locale;
import java.util.Optional;

public abstract class EnumUnlistedProperty<E extends Enum<E>> extends AbstractUnlistedProperty<E> {

    private final Class<E> enumClass;

    protected EnumUnlistedProperty(String name, Class<E> enumClass) {
        this(name, enumClass, getFirstConstant(enumClass));
    }

    protected EnumUnlistedProperty(String name, Class<E> enumClass, E defaultValue) {
        super(name, enumClass, defaultValue);
        this.enumClass = enumClass;
    }

    private static <E extends Enum<E>> E getFirstConstant(Class<E> enumClass) {
        E[] constants = enumClass.getEnumConstants();
        if (constants == null || constants.length == 0) {
            throw new IllegalArgumentException("Enum must have at least one constant: " + enumClass.getName());
        }
        return constants[0];
    }

    @Override
    public Optional<E> parseValue(String value) {
        try {
            return Optional.of(Enum.valueOf(this.enumClass, value.toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public String valueToString(E value) {
        return value.name()
            .toLowerCase(Locale.ROOT);
    }
}
