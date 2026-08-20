package ruiseki.okcore.datastructure;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NonNullList<E> extends AbstractList<E> {

    private final List<E> delegate;
    private final E defaultElement;

    public static <E> NonNullList<E> create() {
        return new NonNullList<E>();
    }

    @SuppressWarnings("unchecked")
    public static <E> NonNullList<E> withSize(int size, @Nullable E fill) {
        Object[] aobject = new Object[size];
        if (fill != null) Arrays.fill(aobject, fill);
        return new NonNullList<E>(Arrays.asList((E[]) aobject), fill);
    }

    @SafeVarargs
    public static <E> NonNullList<E> from(E defaultElementIn, E... elements) {
        return new NonNullList<E>(Arrays.asList(elements), defaultElementIn);
    }

    protected NonNullList() {
        this(new ArrayList<>(), null);
    }

    protected NonNullList(List<E> delegateIn, @Nullable E listType) {
        this.delegate = delegateIn;
        this.defaultElement = listType;
    }

    @NotNull
    @Override
    public E get(int index) {
        return this.delegate.get(index);
    }

    @Override
    public E set(int index, E element) {
        return this.delegate.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        this.delegate.add(index, element);
    }

    @Override
    public E remove(int index) {
        return this.delegate.remove(index);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public void clear() {
        if (this.defaultElement == null) {
            super.clear();
        } else {
            for (int i = 0; i < this.size(); ++i) {
                this.set(i, this.defaultElement);
            }
        }
    }
}
