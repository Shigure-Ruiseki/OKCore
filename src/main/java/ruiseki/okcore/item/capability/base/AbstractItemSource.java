package ruiseki.okcore.item.capability.base;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.IInventoryIterator;
import ruiseki.okcore.item.IItemStack2IntFunction;
import ruiseki.okcore.item.IItemStackPredicate;
import ruiseki.okcore.item.capability.IItemSource;

public abstract class AbstractItemSource implements IItemSource {

    protected int[] allowedSourceSlots;

    @Override
    public void resetSource() {
        allowedSourceSlots = null;
    }

    @Override
    public void setAllowedSourceSlots(int @Nullable [] slots) {
        allowedSourceSlots = slots;
    }

    @Override
    public @Nullable ItemStack pull(@Nullable IItemStackPredicate filter, @Nullable IItemStack2IntFunction amount) {
        IInventoryIterator iter = sourceIterator();

        while (iter.hasNext()) {
            IImmutableItemStack stack = iter.next();

            if (stack == null || stack.isEmpty()) continue;

            if (filter == null || filter.test(stack)) {
                int toExtract = amount == null ? stack.getStackSize() : amount.apply(stack);

                return iter.extract(toExtract, false);
            }
        }

        return null;
    }

    @Override
    public @NotNull IInventoryIterator sourceIterator() {
        return iterator(allowedSourceSlots);
    }

    @NotNull
    protected abstract IInventoryIterator iterator(int[] allowedSlots);
}
