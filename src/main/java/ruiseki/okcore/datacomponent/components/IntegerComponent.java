package ruiseki.okcore.datacomponent.components;

import java.lang.reflect.Type;

import ruiseki.okcore.datacomponent.core.DataComponentType;

public interface IntegerComponent extends DataComponentType<Integer> {

    @Override
    default Type getType() {
        return int.class;
    }
}
