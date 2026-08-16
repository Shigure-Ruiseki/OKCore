package ruiseki.commoncapabilities.api.ingredient.capability;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.okcore.event.capabilities.AttachCapabilitiesEvent;

/**
 * Event for when an {@link IngredientComponent} is being constructed.
 * 
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class AttachCapabilitiesEventIngredientComponent<T, M>
    extends AttachCapabilitiesEvent<IngredientComponent<T, M>> {

    public AttachCapabilitiesEventIngredientComponent(IngredientComponent<T, M> ingredientComponent) {
        super((Class<IngredientComponent<T, M>>) (Class) IngredientComponent.class, ingredientComponent);
    }

    public IngredientComponent<T, M> getIngredientComponent() {
        return getObject();
    }
}
