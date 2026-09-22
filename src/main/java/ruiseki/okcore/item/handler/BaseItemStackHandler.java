package ruiseki.okcore.item.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.apache.commons.lang3.ArrayUtils;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.inventory.RecipeInventory;
import ruiseki.okcore.inventory.SimpleInventory;

public class BaseItemStackHandler extends ItemStackHandler {

    private final Runnable onContentsChanged;
    private final Map<Integer, Integer> slotSizeMap;
    private final RecipeInventory recipeWrapper;
    private BiFunction<Integer, ItemStack, Boolean> canInsert = null;
    private Function<Integer, Boolean> canExtract = null;
    private int maxStackSize = 64;
    private int[] inputSlots = null;
    private int[] outputSlots = null;

    public BaseItemStackHandler() {
        this(1);
    }

    public BaseItemStackHandler(int size) {
        this(size, null);
    }

    protected BaseItemStackHandler(int size, Runnable onContentsChanged) {
        super(size);
        this.onContentsChanged = onContentsChanged;
        this.slotSizeMap = new HashMap<>();
        this.recipeWrapper = new RecipeInventory(this);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return this.insertItem(slot, stack, simulate, false);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate, boolean container) {
        if (!container) {
            if (this.outputSlots != null && ArrayUtils.contains(this.outputSlots, slot)) {
                return stack;
            }

            if (this.inputSlots != null && !ArrayUtils.contains(this.inputSlots, slot)) {
                return stack;
            }

            if (this.canInsert != null && !this.canInsert.apply(slot, stack)) {
                return stack;
            }
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.extractItem(slot, amount, simulate, false);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate, boolean container) {
        if (!container) {
            if (this.canExtract != null && !this.canExtract.apply(slot)) {
                return ItemHelpers.EMPTY;
            }

            if (this.outputSlots != null && !ArrayUtils.contains(this.outputSlots, slot)) {
                return ItemHelpers.EMPTY;
            }
        }

        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.slotSizeMap.getOrDefault(slot, this.maxStackSize);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (this.inputSlots != null && !ArrayUtils.contains(this.inputSlots, slot)) {
            return false;
        }
        if (this.outputSlots != null && ArrayUtils.contains(this.outputSlots, slot)) {
            return false;
        }
        return this.canInsert == null || this.canInsert.apply(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (this.onContentsChanged != null) this.onContentsChanged.run();
    }

    public List<ItemStack> getStacks() {
        return this.stacks;
    }

    public int[] getInputSlots() {
        return this.inputSlots;
    }

    public int[] getOutputSlots() {
        return this.outputSlots;
    }

    public void setDefaultSlotLimit(int size) {
        this.maxStackSize = size;
    }

    public void addSlotLimit(int slot, int size) {
        if (size > 64 && size % 64 != 0) {
            throw new IllegalArgumentException("Slot limits above 64 must be a multiple of 64");
        }

        this.slotSizeMap.put(slot, size);
    }

    public void setCanInsert(BiFunction<Integer, ItemStack, Boolean> validator) {
        this.canInsert = validator;
    }

    public void setCanExtract(Function<Integer, Boolean> canExtract) {
        this.canExtract = canExtract;
    }

    public void setInputSlots(int... slots) {
        this.inputSlots = slots;
    }

    public void setOutputSlots(int... slots) {
        this.outputSlots = slots;
    }

    public IInventory toIInventory() {
        return new SimpleInventory(this.stacks.toArray(new ItemStack[0]));
    }

    public RecipeInventory asRecipeWrapper() {
        return this.recipeWrapper;
    }

    public RecipeInventory toRecipeInventory(int start, int size) {
        return new RecipeInventory(this, start, size);
    }

    public BaseItemStackHandler copy() {
        var newInventory = new BaseItemStackHandler(this.getSlots(), this.onContentsChanged);

        newInventory.setDefaultSlotLimit(this.maxStackSize);
        newInventory.setCanInsert(this.canInsert);
        newInventory.setCanExtract(this.canExtract);
        newInventory.setInputSlots(this.inputSlots);
        newInventory.setOutputSlots(this.outputSlots);

        this.slotSizeMap.forEach(newInventory::addSlotLimit);

        for (int i = 0; i < this.getSlots(); i++) {
            var stack = this.getStackInSlot(i);
            newInventory.setStackInSlot(i, stack.copy());
        }

        return newInventory;
    }

    public static BaseItemStackHandler create(int size) {
        return create(size, builder -> {});
    }

    public static BaseItemStackHandler create(int size, Runnable onContentsChanged) {
        return create(size, onContentsChanged, builder -> {});
    }

    public static BaseItemStackHandler create(int size, Consumer<BaseItemStackHandler> builder) {
        return create(size, null, builder);
    }

    public static BaseItemStackHandler create(int size, Runnable onContentsChanged,
        Consumer<BaseItemStackHandler> builder) {
        var handler = new BaseItemStackHandler(size, onContentsChanged);
        builder.accept(handler);
        return handler;
    }
}
