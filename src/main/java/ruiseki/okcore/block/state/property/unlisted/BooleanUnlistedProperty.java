package ruiseki.okcore.block.state.property.unlisted;

import java.util.Optional;

public abstract class BooleanUnlistedProperty extends AbstractUnlistedProperty<Boolean> {

    protected BooleanUnlistedProperty(String name) {
        super(name, Boolean.class);
    }

    @Override
    public Optional<Boolean> parseValue(String value) {
        if ("true".equalsIgnoreCase(value)) {
            return Optional.of(true);
        }

        if ("false".equalsIgnoreCase(value)) {
            return Optional.of(false);
        }

        return Optional.empty();
    }
}
