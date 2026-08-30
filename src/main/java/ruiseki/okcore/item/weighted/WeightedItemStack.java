package ruiseki.okcore.item.weighted;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.ItemHelpers;

public class WeightedItemStack extends WeightedStackBase {

    private final ItemStack stack;

    public WeightedItemStack(ItemStack stack, double weight) {
        super(weight, weight); // Default: focused weight = normal weight
        this.stack = ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
    }

    public WeightedItemStack(ItemStack stack, double weight, double focusedWeight) {
        super(weight, focusedWeight);
        this.stack = ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
    }

    @Override
    public boolean isStackEqual(ItemStack other) {
        if (ItemHelpers.isEmpty(this.stack) || ItemHelpers.isEmpty(other)) {
            return false;
        }
        return ItemHelpers.areItemsEqual(this.stack, other);
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
        return new WeightedItemStack(ItemHelpers.copy(this.stack), this.realWeight, this.realFocusedWeight);
    }
}
