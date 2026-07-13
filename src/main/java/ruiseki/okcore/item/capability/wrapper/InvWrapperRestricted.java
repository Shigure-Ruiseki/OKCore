package ruiseki.okcore.item.capability.wrapper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;

public class InvWrapperRestricted extends InvWrapper {

    private List<Integer> slotsAllowedInsert;
    private List<Integer> slotsAllowedExtract;

    public InvWrapperRestricted(ISidedInventory inv) {
        super(inv);
        slotsAllowedInsert = new ArrayList<>();
        slotsAllowedExtract = new ArrayList<>();
    }

    public List<Integer> getSlotsExtract() {
        return slotsAllowedExtract;
    }

    public void setSlotsExtract(List<Integer> slotsExport) {
        this.slotsAllowedExtract = slotsExport;
    }

    public List<Integer> getSlotsInsert() {
        return slotsAllowedInsert;
    }

    public void setSlotsInsert(List<Integer> slotsImport) {
        this.slotsAllowedInsert = slotsImport;
    }

    public boolean canInsert(int slot) {
        return this.getSlotsInsert()
            .contains(slot);
    }

    public boolean canExtract(int slot) {
        return this.getSlotsExtract()
            .contains(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!canInsert(slot)) return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!canExtract(slot)) return null;
        return super.extractItem(slot, amount, simulate);
    }
}
