package ruiseki.okcore.datacomponent.componenttype;

import java.lang.reflect.Type;

import ruiseki.okcore.datacomponent.core.DataComponentType;

public interface FloatComponent extends DataComponentType<Float> {

    @Override
    default Type getType() {
        return float.class;
    }
}
