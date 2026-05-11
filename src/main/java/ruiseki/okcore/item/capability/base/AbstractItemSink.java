package ruiseki.okcore.item.capability.base;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.IInventoryIterator;
import ruiseki.okcore.item.InsertionItemStack;
import ruiseki.okcore.item.capability.IItemSink;

public abstract class AbstractItemSink implements IItemSink {

    protected int[] allowedSinkSlots;

    @Override
    public void resetSink() {
        allowedSinkSlots = null;
    }

    @Override
    public void setAllowedSinkSlots(int @Nullable [] slots) {
        allowedSinkSlots = slots;
    }

    @Override
    public int store(IImmutableItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0;

        IInventoryIterator iter = sinkIterator();

        InsertionItemStack insertion = new InsertionItemStack(stack);

        while (iter.hasNext()) {
            iter.next();

            insertion.set(iter.insert(insertion, false));

            if (insertion.isEmpty()) return 0;
        }

        return insertion.getStackSize();
    }

    @Override
    public @NotNull IInventoryIterator sinkIterator() {
        return iterator(allowedSinkSlots);
    }

    @NotNull
    protected abstract IInventoryIterator iterator(int[] allowedSlots);

    @Override
    public abstract @Nullable IInventoryIterator simulatedSinkIterator();
}
