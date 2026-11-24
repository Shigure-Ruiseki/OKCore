package ruiseki.okcore.block.state.property.listed;

import java.util.Collection;

public final class StringProperty extends AbstractListedProperty<String> {

    private StringProperty(String name, Collection<String> values) {
        super(name, String.class, values, value -> value);
    }

    public static StringProperty create(String name, Collection<String> values) {
        return new StringProperty(name, values);
    }
}
