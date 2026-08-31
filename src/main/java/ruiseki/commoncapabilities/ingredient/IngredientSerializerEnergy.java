package ruiseki.commoncapabilities.ingredient;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagLong;

import ruiseki.commoncapabilities.api.ingredient.IIngredientSerializer;

/**
 * Serializer for energy.
 *
 * @author rubensworks
 */
public class IngredientSerializerEnergy implements IIngredientSerializer<Long, Boolean> {

    @Override
    public NBTBase serializeInstance(Long instance) {
        return new NBTTagLong(instance);
    }

    @Override
    public Long deserializeInstance(NBTBase tag) throws IllegalArgumentException {
        if (!(tag instanceof NBTTagLong)) {
            throw new IllegalArgumentException("This deserializer only accepts NBTTagInt");
        }
        return ((NBTTagLong) tag).func_150291_c();
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
