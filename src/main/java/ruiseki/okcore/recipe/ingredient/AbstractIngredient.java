package ruiseki.okcore.recipe.ingredient;

import java.util.stream.Stream;

import com.google.gson.JsonElement;

/**
 * Extension of {@link Ingredient} which makes most methods custom ingredients need to implement abstract, and removes
 * the static constructors
 * Mods are encouraged to extend this class for their custom ingredients
 */
public abstract class AbstractIngredient extends Ingredient {

    /** Empty constructor, for the sake of dynamic ingredients */
    protected AbstractIngredient() {
        super(Stream.of());
    }

    /** Value constructor, for ingredients that have some vanilla representation */
    protected AbstractIngredient(Stream<? extends IItemList> values) {
        super(values);
    }

    @Override
    public abstract boolean isSimple();

    @Override
    public abstract IIngredientSerializer<? extends Ingredient> getSerializer();

    @Override
    public abstract JsonElement toJson();
}
