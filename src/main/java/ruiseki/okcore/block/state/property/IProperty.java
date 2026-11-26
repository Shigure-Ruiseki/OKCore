package ruiseki.okcore.block.state.property;

import java.util.Optional;

public interface IProperty<T extends Comparable<T>> {

    String getName();

    Class<T> getType();

    boolean isValid(T value);

    String valueToString(T value);

    Optional<T> parseValue(String value);
}
