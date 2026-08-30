package ruiseki.okcore.item.weighted;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.ItemStackHelpers;

public class WeightedItemStack extends WeightedStackBase {

    private final ItemStack stack;

    public WeightedItemStack(ItemStack stack, double weight) {
        super(weight, weight); // Default: focused weight = normal weight
        this.stack = ItemStackHelpers.isEmpty(stack) ? ItemStackHelpers.EMPTY : stack;
    }

    public WeightedItemStack(ItemStack stack, double weight, double focusedWeight) {
        super(weight, focusedWeight);
        this.stack = ItemStackHelpers.isEmpty(stack) ? ItemStackHelpers.EMPTY : stack;
    }

    @Override
    public boolean isStackEqual(ItemStack other) {
        if (ItemStackHelpers.isEmpty(this.stack) || ItemStackHelpers.isEmpty(other)) {
            return false;
        }
        return ItemStackHelpers.areStacksEqual(this.stack, other);
    }

    public ItemStack getItemStack() {
        return getMainStack();
    }

    @Override
    public ItemStack getMainStack() {
        return this.stack;
    }

    @Override
    public WeightedStackBase copy() {
        return new WeightedItemStack(ItemStackHelpers.copy(this.stack), this.realWeight, this.realFocusedWeight);
    }
}
