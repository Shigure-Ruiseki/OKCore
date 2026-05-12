package ruiseki.okcore.item.capability.mfr;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import ruiseki.okcore.item.IInventoryIterator;
import ruiseki.okcore.item.capability.base.AbstractItemSink;

public class DSUItemSink extends AbstractItemSink {

    public final IDeepStorageUnit dsu;

    public DSUItemSink(IDeepStorageUnit dsu) {
        this.dsu = dsu;
    }

    @Override
    protected @NotNull IInventoryIterator iterator(int[] allowedSlots) {
        return new DSUInventoryIterator(dsu, allowedSlots, false);
    }

    @Override
    public @Nullable IInventoryIterator simulatedSinkIterator() {
        return new DSUInventoryIterator(dsu, allowedSinkSlots, true);
    }
}
