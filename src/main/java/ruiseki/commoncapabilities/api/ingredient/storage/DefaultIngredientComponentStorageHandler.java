package ruiseki.commoncapabilities.api.ingredient.storage;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;

/**
 * Default implementation of {@link IIngredientComponentStorageHandler}.
 * 
 * @author rubensworks
 */
public class DefaultIngredientComponentStorageHandler implements IIngredientComponentStorageHandler {

    private final Map<IngredientComponent<?, ?>, IIngredientComponentStorage<?, ?>> storages;

    public DefaultIngredientComponentStorageHandler() {
        this.storages = Maps.newIdentityHashMap();
    }

    @Override
    public Collection<IngredientComponent<?, ?>> getComponents() {
        return storages.keySet();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T, M> IIngredientComponentStorage<T, M> getStorage(IngredientComponent<T, M> ingredientComponent) {
        return (IIngredientComponentStorage<T, M>) storages.get(ingredientComponent);
    }

    /**
     * Set the storage for the given ingredient component.
     * 
     * @param ingredientComponent The ingredient component.
     * @param storage             The ingredient component storage, or null if it should be removed.
     * @param <T>                 The instance type.
     * @param <M>                 The matching condition parameter, may be Void. Instances MUST properly implement the
     *                            equals method.
     */
    public <T, M> void setStorage(IngredientComponent<T, M> ingredientComponent,
        @Nullable IIngredientComponentStorage<T, M> storage) {
        if (storage == null) {
            storages.remove(ingredientComponent);
        } else {
            storages.put(ingredientComponent, storage);
        }
    }
}
