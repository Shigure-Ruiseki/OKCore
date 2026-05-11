package ruiseki.okcore.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.jetbrains.annotations.NotNull;

import ruiseki.okcore.helper.ItemStackHelpers;

/**
 * An ImmutableItemStack backed by an ItemStack. This is meant to be allocated once, then modified and returned from an
 * API repeatedly.
 */
public class FastImmutableItemStack implements IImmutableItemStack {

    public ItemStack stack;

    public FastImmutableItemStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public @NotNull Item getItem() {
        // noinspection DataFlowIssue
        return stack.getItem();
    }

    @Override
    public int getItemMeta() {
        return ItemStackHelpers.getStackMeta(stack);
    }

    @Override
    public int getStackSize() {
        return stack.stackSize;
    }

    @Override
    public NBTTagCompound getTag() {
        return stack.getTagCompound();
    }
}
