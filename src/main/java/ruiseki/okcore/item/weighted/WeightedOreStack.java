package ruiseki.okcore.item.weighted;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import ruiseki.okcore.helper.ItemStackHelpers;

public class WeightedOreStack extends WeightedStackBase {

    private final List<ItemStack> stacks;
    private final String oreName;

    public WeightedOreStack(String oreName, double weight) {
        this(oreName, weight, weight);
    }

    public WeightedOreStack(String oreName, double weight, double focusedWeight) {
        super(weight, focusedWeight);
        this.oreName = oreName;
        this.stacks = OreDictionary.getOres(oreName);
    }

    protected WeightedOreStack(String oreName, List<ItemStack> stacks, double weight, double focusedWeight) {
        super(weight, focusedWeight);
        this.oreName = oreName;
        this.stacks = stacks;
    }

    @Override
    public boolean isStackEqual(ItemStack stack) {
        ItemStack mainStack = getMainStack();
        if (ItemStackHelpers.isEmpty(mainStack) || ItemStackHelpers.isEmpty(stack)) {
            return false;
        }
        return ItemStackHelpers.areStacksEqual(mainStack, stack);
    }

    @Override
    public ItemStack getMainStack() {
        if (this.stacks != null && !this.stacks.isEmpty()) {
            ItemStack firstStack = this.stacks.get(0);
            return ItemStackHelpers.isEmpty(firstStack) ? ItemStackHelpers.EMPTY : firstStack;
        }
        return ItemStackHelpers.EMPTY;
    }

    @Override
    public WeightedStackBase copy() {
        List<ItemStack> newStacks = new ArrayList<>();

        if (this.stacks != null) {
            for (ItemStack itemStack : this.stacks) {
                newStacks.add(ItemStackHelpers.copy(itemStack));
            }
        }

        return new WeightedOreStack(this.oreName, newStacks, this.realWeight, this.realFocusedWeight);
    }

    public String getOreName() {
        return oreName;
    }

    public List<ItemStack> getStacks() {
        return stacks;
    }
}
