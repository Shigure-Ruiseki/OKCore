package ruiseki.okcore.block.state.property.unlisted;

import java.util.Locale;
import java.util.Optional;

public abstract class EnumUnlistedProperty<E extends Enum<E>> extends AbstractUnlistedProperty<E> {

    private final Class<E> enumClass;

    protected EnumUnlistedProperty(String name, Class<E> enumClass) {
        super(name, enumClass);
        this.enumClass = enumClass;
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
