package ruiseki.okcore.block.state;

import java.util.Collection;
import java.util.Optional;

public interface IListedProperty<T extends Comparable<T>> extends IProperty<T> {

    Collection<T> getAllowedValues();

    Optional<T> parseValue(String value);

    String getName(T value);
}
