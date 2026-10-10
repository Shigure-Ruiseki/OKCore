package ruiseki.okcore.item.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.MathHelpers;

public class InventoryHandler extends ItemStackHandler {

    public static final String INVENTORY_TAG = "inventory";
    private static final String REAL_COUNT_TAG = "realCount";
    private final NBTTagCompound contentsNbt;
    private final Runnable saveHandler;
    private final List<IntConsumer> onContentsChangedListeners = new ArrayList<>();
    private boolean persistent = true;
    private final Map<Integer, NBTTagCompound> stackNbts = new LinkedHashMap<>();

    private int baseSlotLimit;
    private int slotLimit;
    private double maxStackSizeMultiplier;
    private boolean slotLimitInitialized = false;

    protected InventoryHandler(int numberOfInventorySlots, NBTTagCompound contentsNbt, Runnable saveHandler,
        int baseSlotLimit) {
        super(numberOfInventorySlots);
        this.contentsNbt = contentsNbt;
        this.saveHandler = saveHandler;
        setBaseSlotLimit(baseSlotLimit);
        deserializeNBT(contentsNbt.getCompoundTag(INVENTORY_TAG));
        initStackNbts();
    }

    private void initStackNbts() {
        for (int slot = 0; slot < stacks.size(); slot++) {
            ItemStack slotStack = stacks.get(slot);
            if (!ItemHelpers.isEmpty(slotStack)) {
                stackNbts.put(slot, getSlotsStackNbt(slot, slotStack));
            }
        }
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        if (persistent && updateSlotNbt(slot)) {
            saveInventory();
            triggerOnChangeListeners(slot);
        }
    }

    public void triggerOnChangeListeners(int slot) {
        for (IntConsumer onContentsChangedListener : onContentsChangedListeners) {
            onContentsChangedListener.accept(slot);
        }
    }

    private boolean updateSlotNbt(int slot) {
        ItemStack slotStack = getStackInSlot(slot);
        if (ItemHelpers.isEmpty(slotStack)) {
            if (stackNbts.containsKey(slot)) {
                stackNbts.remove(slot);
                return true;
            }
        } else {
            NBTTagCompound itemTag = getSlotsStackNbt(slot, slotStack);
            if (!stackNbts.containsKey(slot) || !stackNbts.get(slot)
                .equals(itemTag)) {
                stackNbts.put(slot, itemTag);
                return true;
            }
        }
        return false;
    }

    private NBTTagCompound getSlotsStackNbt(int slot, ItemStack slotStack) {
        NBTTagCompound itemTag = new NBTTagCompound();
        itemTag.setInteger("Slot", slot);
        itemTag.setInteger(REAL_COUNT_TAG, slotStack.stackSize);
        slotStack.writeToNBT(itemTag);
        return itemTag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        super.deserializeNBT(nbt);
        setSize(nbt.hasKey("Size", Constants.NBT.TAG_INT) ? nbt.getInteger("Size") : stacks.size());
        NBTTagList tagList = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");

            if (slot >= 0 && slot < stacks.size()) {
                ItemStack slotStack = ItemStack.loadItemStackFromNBT(itemTags);
                if (itemTags.hasKey(REAL_COUNT_TAG)) {
                    ItemHelpers.setCount(slotStack, itemTags.getInteger(REAL_COUNT_TAG));
                }
                stacks.set(slot, slotStack);
            }
        }
        onLoad();
    }

    public int getBaseSlotLimit() {
        return baseSlotLimit;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (!slotLimitInitialized) {
            slotLimitInitialized = true;
            updateSlotLimit();
        }

        return Math.max(slotLimit, baseSlotLimit);
    }

    public int getBaseStackLimit(ItemStack stack) {
        int limit = MathHelpers.intMaxCappedMultiply(stack.getMaxStackSize(), (baseSlotLimit / 64));
        int remainder = baseSlotLimit % 64;
        if (remainder > 0) {
            limit = MathHelpers.intMaxCappedAddition(limit, remainder * stack.getMaxStackSize() / 64);
        }
        return limit;
    }

    public void setBaseSlotLimit(int baseSlotLimit) {
        slotLimitInitialized = false; // not the most ideal of places to do this, but base slot limit is set when
                                      // upgrades change and that's when slot limit needs to be reinitialized as well
        this.baseSlotLimit = baseSlotLimit;
        maxStackSizeMultiplier = baseSlotLimit / 64f;
    }

    private void updateSlotLimit() {
        AtomicInteger slotLimitOverride = new AtomicInteger(baseSlotLimit);
        slotLimit = slotLimitOverride.get();
    }

    public void saveInventory() {
        contentsNbt.setTag(INVENTORY_TAG, serializeNBT());
        saveHandler.run();
    }

    public void addListener(IntConsumer onContentsChanged) {
        onContentsChangedListeners.add(onContentsChanged);
    }

    public void clearListeners() {
        onContentsChangedListeners.clear();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList nbtTagList = new NBTTagList();
        for (NBTTagCompound tag : stackNbts.values()) {
            nbtTagList.appendTag(tag);
        }
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", getSlots());
        return nbt;
    }

    public double getStackSizeMultiplier() {
        return maxStackSizeMultiplier;
    }

    public void changeSlots(int diff) {
        List<ItemStack> previousStacks = stacks;
        stacks = NonNullList.withSize(previousStacks.size() + diff, ItemHelpers.EMPTY);
        for (int slot = 0; slot < previousStacks.size() && slot < stacks.size(); slot++) {
            stacks.set(slot, previousStacks.get(slot));
        }
        initStackNbts();
        saveInventory();
    }

}
