package ruiseki.commoncapabilities.ingredient;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;

import ruiseki.commoncapabilities.api.ingredient.IIngredientSerializer;

/**
 * Serializer for energy.
 * 
 * @author rubensworks
 */
public class IngredientSerializerEnergy implements IIngredientSerializer<Integer, Boolean> {

    @Override
    public NBTBase serializeInstance(Integer instance) {
        return new NBTTagInt(instance);
    }

    @Override
    public Integer deserializeInstance(NBTBase tag) throws IllegalArgumentException {
        if (!(tag instanceof NBTTagInt)) {
            throw new IllegalArgumentException("This deserializer only accepts NBTTagInt");
        }
        return ((NBTTagInt) tag).func_150287_d();
    }

    @Override
    public NBTBase serializeCondition(Boolean matchCondition) {
        return new NBTTagByte((byte) (matchCondition ? 1 : 0));
    }

    @Override
    public Boolean deserializeCondition(NBTBase tag) throws IllegalArgumentException {
        if (!(tag instanceof NBTTagByte)) {
            throw new IllegalArgumentException("This deserializer only accepts NBTTagByte");
        }
        return ((NBTTagByte) tag).func_150290_f() == 1;
    }
}
