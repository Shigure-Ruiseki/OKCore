package ruiseki.okcore.item.capability.wrapper;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.inventory.EntityEquipmentSlot;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

/**
 * Exposes the armor or hands inventory of an {@link EntityLivingBase} as an {@link IItemHandler} using
 * {@link EntityLivingBase#getEquipmentInSlot(int)} and
 * {@link EntityLivingBase#setCurrentItemOrArmor(int, ItemStack)}.
 */
public abstract class EntityEquipmentInvWrapper implements IItemHandlerModifiable {

    /**
     * The entity.
     */
    protected final EntityLivingBase entity;

    /**
     * The slots exposed by this wrapper, with {@link EntityEquipmentSlot#getIndex()} as the index.
     */
    protected final List<EntityEquipmentSlot> slots;

    /**
     * @param entity   The entity.
     * @param slotType The slot type to expose.
     */
    public EntityEquipmentInvWrapper(final EntityLivingBase entity, final EntityEquipmentSlot.Type slotType) {
        this.entity = entity;
        final List<EntityEquipmentSlot> slots = new ArrayList<EntityEquipmentSlot>();
        for (final EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
            if (slot.getSlotType() == slotType) {
                slots.add(slot);
            }
        }
        this.slots = ImmutableList.copyOf(slots);
    }

    @Override
    public int getSlots() {
        return slots.size();
    }

    @Override
    public ItemStack getStackInSlot(final int slot) {
        return entity.getEquipmentInSlot(validateSlotIndex(slot).getSlotIndex());
    }

    @Override
    public ItemStack insertItem(final int slot, final ItemStack stack, final boolean simulate) {
        if (ItemHelpers.isEmpty(stack)) return ItemHelpers.EMPTY;

        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getEquipmentInSlot(equipmentSlot.getSlotIndex());

        int limit = getStackLimit(slot, stack);

        if (!ItemHelpers.isEmpty(existing)) {
            if (!ItemHandlerHelpers.canItemStacksStack(stack, existing)) return stack;

            limit -= ItemHelpers.getCount(existing);
        }

        if (limit <= 0) return stack;

        boolean reachedLimit = ItemHelpers.getCount(stack) > limit;

        if (!simulate) {
            if (ItemHelpers.isEmpty(existing)) {
                entity.setCurrentItemOrArmor(
                    equipmentSlot.getSlotIndex(),
                    reachedLimit ? ItemHandlerHelpers.copyStackWithSize(stack, limit) : stack);
            } else {
                ItemHelpers.grow(existing, reachedLimit ? limit : ItemHelpers.getCount(stack));
            }
        }

        return reachedLimit ? ItemHandlerHelpers.copyStackWithSize(stack, ItemHelpers.getCount(stack) - limit)
            : ItemHelpers.EMPTY;
    }

    @Override
    public ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
        if (amount == 0) return ItemHelpers.EMPTY;

        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);

        final ItemStack existing = entity.getEquipmentInSlot(equipmentSlot.getSlotIndex());

        if (ItemHelpers.isEmpty(existing)) return ItemHelpers.EMPTY;

        final int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (ItemHelpers.getCount(existing) <= toExtract) {
            if (!simulate) {
                entity.setCurrentItemOrArmor(equipmentSlot.getSlotIndex(), ItemHelpers.EMPTY);
            }

            return existing;
        } else {
            if (!simulate) {
                entity.setCurrentItemOrArmor(
                    equipmentSlot.getSlotIndex(),
                    ItemHandlerHelpers.copyStackWithSize(existing, ItemHelpers.getCount(existing) - toExtract));
            }

            return ItemHandlerHelpers.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(final int slot) {
        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);
        return equipmentSlot.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? 1 : 64;
    }

    protected int getStackLimit(final int slot, final ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public void setStackInSlot(final int slot, final ItemStack stack) {
        final EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);
        if (ItemStack.areItemStacksEqual(entity.getEquipmentInSlot(equipmentSlot.getSlotIndex()), stack)) return;
        entity.setCurrentItemOrArmor(equipmentSlot.getSlotIndex(), stack);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) return false;
        EntityEquipmentSlot equipmentSlot = validateSlotIndex(slot);
        return ItemHelpers.getItem(stack) instanceof ItemArmor armor && armor.armorType == equipmentSlot.getIndex();
    }

    protected EntityEquipmentSlot validateSlotIndex(final int slot) {
        if (slot < 0 || slot >= slots.size())
            throw new IllegalArgumentException("Slot " + slot + " not in valid range - [0," + slots.size() + ")");

        return slots.get(slot);
    }
}
