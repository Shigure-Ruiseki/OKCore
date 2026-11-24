package ruiseki.okcore.block.state.property.listed;

import java.util.ArrayList;
import java.util.List;

public final class IntegerProperty extends AbstractListedProperty<Integer> {

    private IntegerProperty(String name, int min, int max) {
        super(name, Integer.class, createValues(min, max), String::valueOf);
    }

    private static List<Integer> createValues(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum cannot be greater than maximum");
        }

        List<Integer> values = new ArrayList<>();

        for (int i = min; i <= max; i++) {
            values.add(i);
        }

        return values;
    }

    public static IntegerProperty create(String name, int min, int max) {
        return new IntegerProperty(name, min, max);
    }
}
