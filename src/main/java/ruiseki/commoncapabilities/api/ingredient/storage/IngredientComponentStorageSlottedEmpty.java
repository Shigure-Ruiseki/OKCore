package ruiseki.commoncapabilities.api.ingredient.storage;

import javax.annotation.Nonnull;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;

/**
 * A dummy slotted ingredient component storage that is empty.
 * 
 * @author rubensworks
 */
public class IngredientComponentStorageSlottedEmpty<T, M> extends IngredientComponentStorageEmpty<T, M>
    implements IIngredientComponentStorageSlotted<T, M> {

    public IngredientComponentStorageSlottedEmpty(IngredientComponent<T, M> component) {
        super(component);
    }

    @Override
    public int getSlots() {
        return 0;
    }

    @Override
    public T getSlotContents(int slot) {
        return getComponent().getMatcher()
            .getEmptyInstance();
    }

    @Override
    public long getMaxQuantity(int slot) {
        return 0;
    }

    @Override
    public T insert(int slot, @Nonnull T ingredient, boolean simulate) {
        return ingredient;
    }

    @Override
    public T extract(int slot, long maxQuantity, boolean simulate) {
        return getComponent().getMatcher()
            .getEmptyInstance();
    }
}
