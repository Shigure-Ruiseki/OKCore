package ruiseki.okcore.datacomponent.componenttype;

import java.lang.reflect.Type;

import ruiseki.okcore.datacomponent.core.DataComponentType;

public interface StringComponent extends DataComponentType<String> {

    @Override
    default Type getType() {
        return String.class;
    }
}
