package ruiseki.okcore.block.state.property.listed;

import java.util.Arrays;

public final class BooleanProperty extends AbstractListedProperty<Boolean> {

    private BooleanProperty(String name) {
        super(name, Boolean.class, Arrays.asList(Boolean.FALSE, Boolean.TRUE), String::valueOf);
    }

    public static BooleanProperty create(String name) {
        return new BooleanProperty(name);
    }
}
