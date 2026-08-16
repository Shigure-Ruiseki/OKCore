package ruiseki.commoncapabilities.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.Constants;

import ruiseki.commoncapabilities.api.ingredient.IIngredientSerializer;

/**
 * Serializer for ItemStacks.
 *
 * @author rubensworks
 */
public class IngredientSerializerItemStack implements IIngredientSerializer<ItemStack, Integer> {

    @Override
    public NBTBase serializeInstance(ItemStack instance) {
        if (instance == null) {
            return new NBTTagCompound();
        }
        NBTTagCompound tag = instance.writeToNBT(new NBTTagCompound());
        if (instance.stackSize > 127) {
            tag.setInteger("ExtendedCount", instance.stackSize);
            tag.setByte("Count", (byte) 1);
        }
        return tag;
    }

    @Override
    public ItemStack deserializeInstance(NBTBase tag) throws IllegalArgumentException {
        if (!(tag instanceof NBTTagCompound)) {
            throw new IllegalArgumentException("This deserializer only accepts NBTTagCompound");
        }
        NBTTagCompound stackTag = (NBTTagCompound) tag;
        if (stackTag.hasNoTags()) {
            return null;
        }
        ItemStack itemStack = ItemStack.loadItemStackFromNBT(stackTag);
        if (itemStack != null && stackTag.hasKey("ExtendedCount", Constants.NBT.TAG_INT)) {
            itemStack.stackSize = stackTag.getInteger("ExtendedCount");
        }
        return itemStack;
    }

    @Override
    public NBTBase serializeCondition(Integer matchCondition) {
        return new NBTTagInt(matchCondition);
    }

    @Override
    public Integer deserializeCondition(NBTBase tag) throws IllegalArgumentException {
        if (!(tag instanceof NBTTagInt)) {
            throw new IllegalArgumentException("This deserializer only accepts NBTTagInt");
        }
        return ((NBTTagInt) tag).func_150287_d();
    }
}
