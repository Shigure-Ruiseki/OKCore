package ruiseki.okcore.datacomponent.components;

import java.lang.reflect.Type;

import ruiseki.okcore.datacomponent.core.DataComponentType;

public interface StringComponent extends DataComponentType<String> {

    @Override
    default Type getType() {
        return String.class;
    }
}
