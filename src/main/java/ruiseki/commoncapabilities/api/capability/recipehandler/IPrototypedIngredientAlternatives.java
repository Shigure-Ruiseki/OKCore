package ruiseki.commoncapabilities.api.capability.recipehandler;

import java.util.Collection;

import net.minecraft.nbt.NBTBase;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;

/**
 * A holder for a list of {@link ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient} alternatives.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void.
 * @author rubensworks
 */
public interface IPrototypedIngredientAlternatives<T, M> {

    public static Byte2ObjectMap<ISerializer<?>> SERIALIZERS = new Byte2ObjectArrayMap<>();

    public Collection<IPrototypedIngredient<T, M>> getAlternatives();

    public ISerializer<?> getSerializer();

    public static interface ISerializer<A extends IPrototypedIngredientAlternatives<?, ?>> {

        public byte getId();

        public <T, M> NBTBase serialize(IngredientComponent<T, M> ingredientComponent, A alternatives);

        public <T, M> A deserialize(IngredientComponent<T, M> ingredientComponent, NBTBase tag);

    }

}
