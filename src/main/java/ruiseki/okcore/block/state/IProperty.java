package ruiseki.okcore.block.state;

public interface IProperty<T extends Comparable<T>> {

    String getName();

    Class<T> getType();

    boolean isValid(T value);

    String valueToString(T value);
}
