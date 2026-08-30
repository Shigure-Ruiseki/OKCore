package ruiseki.okcore.item.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.persist.nbt.INBTSerializable;

public class ItemStackHandler implements IItemHandler, IItemHandlerModifiable, INBTSerializable {

    protected List<ItemStack> stacks;

    public ItemStackHandler() {
        this(1);
    }

    public ItemStackHandler(int size) {
        setSize(size);
    }

    public ItemStackHandler(List<ItemStack> stacks) {
        this.stacks = stacks != null ? stacks : new ArrayList<>();
    }

    public ItemStackHandler(ItemStack[] stacks) {
        this.stacks = stacks != null ? Arrays.asList(stacks) : new ArrayList<>();
    }

    public void setEmpty() {
        this.stacks.replaceAll(ignored -> ItemHelpers.EMPTY);
    }

    public void setSize(int size) {
        ItemStack[] array = new ItemStack[size];
        Arrays.fill(array, ItemHelpers.EMPTY);
        this.stacks = new ArrayList<>(Arrays.asList(array));
    }

    public int[] getSlotArray() {
        return IntStream.range(0, this.getSlots())
            .toArray();
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        this.stacks.set(slot, ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack);
        this.onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return this.stacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        ItemStack stack = this.stacks.get(slot);
        return ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (ItemHelpers.isEmpty(stack)) {
            return ItemHelpers.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            int limit = this.getStackLimit(slot, stack);

            if (!ItemHelpers.isEmpty(existing)) {
                if (!ItemHandlerHelpers.canItemStacksStack(stack, existing)) {
                    return stack;
                }

                limit -= existing.stackSize;
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.stackSize > limit;
                if (!simulate) {
                    if (ItemHelpers.isEmpty(existing)) {
                        this.stacks.set(
                            slot,
                            reachedLimit ? ItemHandlerHelpers.copyStackWithSize(stack, limit)
                                : ItemHelpers.copy(stack));
                    } else {
                        ItemHelpers.grow(existing, reachedLimit ? limit : stack.stackSize);
                    }

                    this.onContentsChanged(slot);
                }

                return reachedLimit ? ItemHandlerHelpers.copyStackWithSize(stack, stack.stackSize - limit)
                    : ItemHelpers.EMPTY;
            }
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) {
            return ItemHelpers.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            if (ItemHelpers.isEmpty(existing)) {
                return ItemHelpers.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.stackSize <= toExtract) {
                    if (!simulate) {
                        this.stacks.set(slot, ItemHelpers.EMPTY);
                        this.onContentsChanged(slot);
                    }

                    return existing;
                } else {
                    if (!simulate) {
                        this.stacks
                            .set(slot, ItemHandlerHelpers.copyStackWithSize(existing, existing.stackSize - toExtract));
                        this.onContentsChanged(slot);
                    }

                    return ItemHandlerHelpers.copyStackWithSize(existing, toExtract);
                }
            }
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @Nullable ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) {
            return 0;
        }
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList nbtTagList = new NBTTagList();

        for (int i = 0; i < this.stacks.size(); ++i) {
            ItemStack stack = this.stacks.get(i);
            if (!ItemHelpers.isEmpty(stack)) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setInteger("Slot", i);
                stack.writeToNBT(itemTag);
                itemTag.setInteger("Count", stack.stackSize);
                nbtTagList.appendTag(itemTag);
            }
        }

        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Items", nbtTagList);
        nbt.setInteger("Size", this.stacks.size());
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.setSize(nbt.hasKey("Size", 3) ? nbt.getInteger("Size") : this.stacks.size());
        NBTTagList tagList = nbt.getTagList("Items", 10);

        for (int i = 0; i < tagList.tagCount(); ++i) {
            NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
            int slot = itemTags.getInteger("Slot");
            if (slot >= 0 && slot < this.stacks.size()) {
                ItemStack loadedStack = ItemStack.loadItemStackFromNBT(itemTags);
                if (!ItemHelpers.isEmpty(loadedStack) && itemTags.hasKey("Count", Constants.NBT.TAG_INT)) {
                    loadedStack.stackSize = itemTags.getInteger("Count");
                }
                this.stacks.set(slot, ItemHelpers.isEmpty(loadedStack) ? ItemHelpers.EMPTY : loadedStack);
            }
        }

        this.onLoad();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    protected void onLoad() {}

    protected void onContentsChanged(int slot) {}

    public ItemStack getAndRemoveSlot(int slot) {
        ItemStack stack = this.getStackInSlot(slot);

        if (ItemHelpers.isEmpty(stack)) {
            return ItemHelpers.EMPTY;
        }

        ItemStack extract = ItemHelpers.copy(stack);
        this.setStackInSlot(slot, ItemHelpers.EMPTY);
        return extract;
    }

    public int voidItem(int slot, int amount) {
        if (amount <= 0) return 0;

        ItemStack stack = getStackInSlot(slot);
        if (ItemHelpers.isEmpty(stack)) return amount;

        int oldCount = stack.stackSize;
        int toVoid = Math.min(oldCount, amount);

        ItemHelpers.shrink(stack, toVoid);
        if (ItemHelpers.isEmpty(stack)) {
            setStackInSlot(slot, ItemHelpers.EMPTY);
        } else {
            setStackInSlot(slot, stack);
        }

        return amount - toVoid;
    }

    public int growItem(int slot, int amount) {
        if (amount <= 0) return 0;

        ItemStack stack = getStackInSlot(slot);
        if (ItemHelpers.isEmpty(stack)) return amount;

        int oldCount = stack.stackSize;
        int max = stack.getMaxStackSize();

        int toAdd = Math.min(amount, max - oldCount);
        ItemHelpers.grow(stack, toAdd);

        setStackInSlot(slot, stack);
        return amount - toAdd;
    }

    public boolean hasRoomForItem(ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) {
            return false;
        }

        int remaining = stack.stackSize;

        for (int i = 0; i < getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);

            if (ItemHelpers.isEmpty(slotStack)) {
                remaining -= Math.min(stack.getMaxStackSize(), remaining);
            } else if (ItemHelpers.areItemsEqual(slotStack, stack)) {
                int space = slotStack.getMaxStackSize() - slotStack.stackSize;
                if (space > 0) {
                    remaining -= Math.min(space, remaining);
                }
            }

            if (remaining <= 0) {
                return true;
            }
        }

        return false;
    }

    public boolean hasEmptySlot() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (ItemHelpers.isEmpty(stack)) {
                return true;
            }
        }
        return false;
    }

    public int addItemToAvailableSlots(ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) {
            return 0;
        }

        int remaining = stack.stackSize;

        for (int i = 0; i < getSlots() && remaining > 0; i++) {
            ItemStack slotStack = getStackInSlot(i);

            if (ItemHelpers.isEmpty(slotStack)) continue;

            if (ItemHelpers.canStack(slotStack, stack)) {
                int max = slotStack.getMaxStackSize();
                int canAdd = max - slotStack.stackSize;

                if (canAdd > 0) {
                    int toAdd = Math.min(canAdd, remaining);
                    ItemHelpers.grow(slotStack, toAdd);
                    setStackInSlot(i, slotStack);
                    remaining -= toAdd;
                }
            }
        }

        for (int i = 0; i < getSlots() && remaining > 0; i++) {
            ItemStack slotStack = getStackInSlot(i);

            if (!ItemHelpers.isEmpty(slotStack)) continue;

            ItemStack newStack = ItemHelpers.copy(stack);
            int toAdd = Math.min(newStack.getMaxStackSize(), remaining);
            newStack.stackSize = toAdd;

            setStackInSlot(i, newStack);
            remaining -= toAdd;
        }

        return remaining;
    }

    public void dropAll(World world, int x, int y, int z) {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!ItemHelpers.isEmpty(stack)) {
                dropStack(world, x, y, z, stack);
            }
        }
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) {
            return;
        }

        float dx = world.rand.nextFloat() * 0.8F + 0.1F;
        float dy = world.rand.nextFloat() * 0.8F + 0.1F;
        float dz = world.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, ItemHelpers.copy(stack));

        float motion = 0.05F;
        entityItem.motionX = world.rand.nextGaussian() * motion;
        entityItem.motionY = world.rand.nextGaussian() * motion + 0.2F;
        entityItem.motionZ = world.rand.nextGaussian() * motion;

        world.spawnEntityInWorld(entityItem);
    }
}
