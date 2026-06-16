package ruiseki.okcore.event.generic;

import java.lang.reflect.Type;

public interface IGenericEvent<T> {

    Type getGenericType();
}
