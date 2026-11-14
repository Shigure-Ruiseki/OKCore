package ruiseki.okcore.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemHelpers;

public interface IItemSharedTag {

    default NBTTagCompound getNBTShareTag(ItemStack stack) {
        return ItemHelpers.getTagCompound(stack);
    }

    default void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        ItemHelpers.setTagCompound(stack, nbt);
    }
}
