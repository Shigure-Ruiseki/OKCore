package ruiseki.okcore.ingredient.collection;

/**
 * A mutable ingredient collection using set semantics.
 * This means that each instances can only be present once in the collection based on its equals method.
 *
 * @see java.util.Set
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 */
public interface IIngredientSetMutable<T, M> extends IIngredientSet<T, M>, IIngredientCollectionMutable<T, M> {

}
