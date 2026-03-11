package ruiseki.okcore.item;

import java.util.List;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemHandlerHelpers;

/**
 * An item handler wrapper for items.
 *
 * @author rubensworks
 */
public abstract class ItemItemStackHandler implements IItemHandlerModifiable {

    private final ItemStack itemStack;

    public ItemItemStackHandler(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    protected abstract List<ItemStack> getItemList();

    protected abstract void setItemList(List<ItemStack> itemStacks);

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        List<ItemStack> itemStacks = getItemList();
        itemStacks.set(slot, stack);
        onContentsChanged(slot);
        setItemList(itemStacks);
    }

    @Override
    public @Nullable ItemStack getStackInSlot(int slot) {
        return getItemList().get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null) {
            return null;
        }

        ItemStack existing = getStackInSlot(slot);
        int limit = getStackLimit(slot, stack);

        if (existing != null) {
            if (!ItemHandlerHelpers.canItemStacksStack(stack, existing)) {
                return stack;
            }

            limit -= existing.stackSize;
        }

        if (limit <= 0) {
            return stack;
        }

        boolean reachedLimit = stack.stackSize > limit;

        if (!simulate) {

            if (existing == null) {

                setStackInSlot(slot, reachedLimit ? ItemHandlerHelpers.copyStackWithSize(stack, limit) : stack.copy());

            } else {

                existing.stackSize += reachedLimit ? limit : stack.stackSize;

                List<ItemStack> itemStacks = getItemList();
                itemStacks.set(slot, existing);
                setItemList(itemStacks);

                onContentsChanged(slot);
            }
        }

        return reachedLimit ? ItemHandlerHelpers.copyStackWithSize(stack, stack.stackSize - limit) : null;
    }

    @Nullable
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {

        if (amount <= 0) {
            return null;
        }

        ItemStack existing = getStackInSlot(slot);

        if (existing == null) {
            return null;
        }

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.stackSize <= toExtract) {

            if (!simulate) {
                setStackInSlot(slot, null);
            }

            return existing;
        }

        if (!simulate) {

            ItemStack remainder = ItemHandlerHelpers.copyStackWithSize(existing, existing.stackSize - toExtract);

            setStackInSlot(slot, remainder);
        }

        return ItemHandlerHelpers.copyStackWithSize(existing, toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @Nullable ItemStack stack) {
        if (stack == null) {
            return 0;
        }
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    protected void onContentsChanged(int slot) {}
}
