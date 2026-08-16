package ruiseki.okcore.ingredient.collection;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;

/**
 * An ingredient list collection that internally uses an {@link LinkedList} to store instances.
 * 
 * @see LinkedList
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 */
public class IngredientLinkedList<T, M> extends IngredientList<T, M> {

    public IngredientLinkedList(IngredientComponent<T, M> component) {
        super(component, new LinkedList<>());
    }

    public IngredientLinkedList(IngredientComponent<T, M> component, Iterable<? extends T> iterable) {
        super(component, Lists.newLinkedList(iterable));
    }

    public IngredientLinkedList(IngredientComponent<T, M> component, Iterator<? extends T> iterable) {
        this(component);
        while (iterable.hasNext()) {
            add(iterable.next());
        }
    }

}
