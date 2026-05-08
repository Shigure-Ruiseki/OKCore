package ruiseki.okcore.datacomponent.componenttype;

import java.lang.reflect.Type;

import ruiseki.okcore.datacomponent.core.DataComponentType;

public interface BooleanComponent extends DataComponentType<Boolean> {

    @Override
    default Type getType() {
        return boolean.class;
    }
}
