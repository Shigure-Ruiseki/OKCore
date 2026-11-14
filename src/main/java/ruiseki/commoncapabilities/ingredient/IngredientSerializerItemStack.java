package ruiseki.commoncapabilities.ingredient;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.common.util.Constants;

import ruiseki.commoncapabilities.api.ingredient.IIngredientSerializer;
import ruiseki.okcore.helper.ItemHelpers;

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
        if (ItemHelpers.getCount(instance) > 127) {
            tag.setInteger("ExtendedCount", ItemHelpers.getCount(instance));
            tag.setByte("Count", (byte) 1);
        }
        return tag;
    }

    @Override
    public ItemStack deserializeInstance(NBTBase tag) throws IllegalArgumentException {
        if (!(tag instanceof NBTTagCompound stackTag)) {
            throw new IllegalArgumentException("This deserializer only accepts NBTTagCompound");
        }
        if (stackTag.hasNoTags()) return null;
        ItemStack itemStack = ItemStack.loadItemStackFromNBT(stackTag);
        if (stackTag.hasKey("ExtendedCount", Constants.NBT.TAG_INT)) {
            ItemHelpers.setCount(itemStack, stackTag.getInteger("ExtendedCount"));
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
