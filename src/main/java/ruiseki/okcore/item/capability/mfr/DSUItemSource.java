package ruiseki.okcore.item.capability.mfr;

import org.jetbrains.annotations.NotNull;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import ruiseki.okcore.item.IInventoryIterator;
import ruiseki.okcore.item.capability.base.AbstractItemSource;

public class DSUItemSource extends AbstractItemSource {

    public final IDeepStorageUnit dsu;

    public DSUItemSource(IDeepStorageUnit dsu) {
        this.dsu = dsu;
    }

    @Override
    protected @NotNull IInventoryIterator iterator(int[] allowedSlots) {
        return new DSUInventoryIterator(dsu, allowedSlots, false);
    }
}
