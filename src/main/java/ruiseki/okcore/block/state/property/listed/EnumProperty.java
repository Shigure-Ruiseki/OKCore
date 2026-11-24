package ruiseki.okcore.block.state.property.listed;

import java.util.Arrays;
import java.util.Locale;

public final class EnumProperty<E extends Enum<E>> extends AbstractListedProperty<E> {

    private EnumProperty(String name, Class<E> enumClass) {
        super(
            name,
            enumClass,
            Arrays.asList(enumClass.getEnumConstants()),
            value -> value.name()
                .toLowerCase(Locale.ROOT));
    }

    public static <E extends Enum<E>> EnumProperty<E> create(String name, Class<E> enumClass) {
        return new EnumProperty<>(name, enumClass);
    }
}
