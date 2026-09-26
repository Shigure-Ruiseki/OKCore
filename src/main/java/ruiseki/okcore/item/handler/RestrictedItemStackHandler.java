package ruiseki.okcore.item.handler;

import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.item.ItemStack;

public class RestrictedItemStackHandler extends BaseItemStackHandler {

    public RestrictedItemStackHandler() {
        this(1);
    }

    public RestrictedItemStackHandler(int size) {
        this(size, null);
    }

    public RestrictedItemStackHandler(int size, Runnable onContentsChanged) {
        super(size, onContentsChanged);
    }

    public void setSlotsInsert(int slot) {
        this.setInputSlots(slot);
    }

    public void setSlotsInsert(int startInclusive, int endInclusive) {
        this.setInputSlots(
            IntStream.rangeClosed(startInclusive, endInclusive)
                .toArray());
    }

    public void setSlotsInsert(List<Integer> slotsAllowedInsert) {
        this.setInputSlots(
            slotsAllowedInsert.stream()
                .mapToInt(Integer::intValue)
                .toArray());
    }

    public List<Integer> getSlotsInsert() {
        int[] inputs = getInputSlots();
        if (inputs == null) return List.of();
        return IntStream.of(inputs)
            .boxed()
            .toList();
    }

    public void setSlotsExtract(int slot) {
        this.setOutputSlots(slot);
    }

    public void setSlotsExtract(int startInclusive, int endInclusive) {
        this.setOutputSlots(
            IntStream.rangeClosed(startInclusive, endInclusive)
                .toArray());
    }

    public void setSlotsExtract(List<Integer> slotsAllowedExtract) {
        this.setOutputSlots(
            slotsAllowedExtract.stream()
                .mapToInt(Integer::intValue)
                .toArray());
    }

    public List<Integer> getSlotsExtract() {
        int[] outputs = getOutputSlots();
        if (outputs == null) return List.of();
        return IntStream.of(outputs)
            .boxed()
            .toList();
    }

    public void setSlotsForBoth(int... slots) {
        this.setInputSlots(slots);
        this.setOutputSlots(slots);
    }

    public void setSlotsForBoth(List<Integer> slots) {
        int[] array = slots.stream()
            .mapToInt(Integer::intValue)
            .toArray();
        this.setInputSlots(array);
        this.setOutputSlots(array);
    }

    public void setSlotsForBoth() {
        if (this.getSlots() > 0) {
            this.setSlotsForBoth(
                IntStream.rangeClosed(0, this.getSlots() - 1)
                    .toArray());
        }
    }

    public boolean canInsert(int slot) {
        int[] inputs = getInputSlots();
        if (inputs == null) return true;
        for (int input : inputs) {
            if (input == slot) return true;
        }
        return false;
    }

    public boolean canExtract(int slot) {
        int[] outputs = getOutputSlots();
        if (outputs == null) return true;
        for (int output : outputs) {
            if (output == slot) return true;
        }
        return false;
    }

    public ItemStack insertItemInternal(int slot, ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate, true);
    }

    public ItemStack extractItemInternal(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate, true);
    }
}
