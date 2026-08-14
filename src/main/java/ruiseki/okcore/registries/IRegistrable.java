package ruiseki.okcore.registries;

import java.util.function.Supplier;

public interface IRegistrable<T> extends Supplier<T> {

    void register(String name);
}
