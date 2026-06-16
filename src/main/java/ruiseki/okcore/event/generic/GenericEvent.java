package ruiseki.okcore.event.generic;

import java.lang.reflect.Type;

import cpw.mods.fml.common.eventhandler.Event;

public class GenericEvent<T> extends Event implements IGenericEvent<T> {

    private Class<T> type;

    protected GenericEvent(Class<T> type) {
        this.type = type;
    }

    @Override
    public Type getGenericType() {
        return type;
    }
}
