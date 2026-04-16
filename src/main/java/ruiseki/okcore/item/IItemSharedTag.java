package ruiseki.okcore.item;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.Nullable;

public interface IItemSharedTag {

    default NBTTagCompound getNBTShareTag(ItemStack stack) {
        return stack.getTagCompound();
    }

    default void readNBTShareTag(ItemStack stack, @Nullable NBTTagCompound nbt) {
        stack.setTagCompound(nbt);
    }
}
