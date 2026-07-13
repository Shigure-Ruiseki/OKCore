package ruiseki.okcore.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class RestrictedItemStackHandler extends ItemStackHandler {

    private List<Integer> slotsAllowedInsert;
    private List<Integer> slotsAllowedExtract;

    public RestrictedItemStackHandler() {
        this(1);
    }

    public RestrictedItemStackHandler(int size) {
        super(size);
        this.slotsAllowedInsert = new ArrayList<>();
        this.slotsAllowedExtract = new ArrayList<>();
    }

    public List<Integer> getSlotsInsert() {
        return slotsAllowedInsert;
    }

    public void setSlotsInsert(List<Integer> slotsAllowedInsert) {
        this.slotsAllowedInsert = slotsAllowedInsert;
    }

    public List<Integer> getSlotsExtract() {
        return slotsAllowedExtract;
    }

    public void setSlotsExtract(List<Integer> slotsAllowedExtract) {
        this.slotsAllowedExtract = slotsAllowedExtract;
    }

    public boolean canInsert(int slot) {
        return this.slotsAllowedInsert.contains(slot);
    }

    public boolean canExtract(int slot) {
        return this.slotsAllowedExtract.contains(slot);
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

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!canInsert(slot)) return false;
        return super.isItemValid(slot, stack);
    }

    public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }
}
