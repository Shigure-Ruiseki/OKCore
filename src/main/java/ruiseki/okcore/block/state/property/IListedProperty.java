package ruiseki.okcore.block.state.property;

import java.util.Collection;

public interface IListedProperty<T extends Comparable<T>> extends IProperty<T> {

    Collection<T> getAllowedValues();

    String getName(T value);
}
