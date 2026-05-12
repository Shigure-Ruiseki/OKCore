package ruiseki.okcore.item.capability.mfr;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.util.ItemUtil;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import ruiseki.okcore.item.IImmutableItemStack;
import ruiseki.okcore.item.capability.base.AbstractInventoryIterator;

public class DSUInventoryIterator extends AbstractInventoryIterator {

    private static final int[] SLOTS = { 0 };

    public final IDeepStorageUnit dsu;
    private final boolean simulated;

    public DSUInventoryIterator(IDeepStorageUnit dsu, int[] allowedSlots, boolean simulated) {
        super(SLOTS, allowedSlots);
        this.dsu = dsu;
        this.simulated = simulated;
    }

    @Override
    protected ItemStack getStackInSlot(int slot) {
        if (slot != 0) return null;

        return ItemUtil.copy(dsu.getStoredItemType());
    }

    @Override
    public @Nullable ItemStack extract(int amount, boolean forced) {
        ItemStack stored = dsu.getStoredItemType();

        if (stored == null) return null;

        int toExtract = Math.min(amount, stored.stackSize);

        if (!simulated) {
            dsu.setStoredItemCount(stored.stackSize - toExtract);
        }

        return ItemUtil.copyAmount(toExtract, stored);
    }

    @Override
    public int insert(IImmutableItemStack stack, boolean forced) {
        ItemStack stored = dsu.getStoredItemType();

        if (stored != null && !stack.matches(stored)) return stack.getStackSize();

        int storedAmount = stored == null ? 0 : stored.stackSize;
        int toInsert = Math.min(stack.getStackSize(), dsu.getMaxStoredCount() - storedAmount);

        if (!simulated) {
            if (stored == null) {
                dsu.setStoredItemType(stack.toStack(1), 1);
            }

            dsu.setStoredItemCount(storedAmount + toInsert);
        }

        return stack.getStackSize() - toInsert;
    }
}
