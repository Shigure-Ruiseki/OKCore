package ruiseki.okcore.inventory.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.item.handler.IItemHandlerModifiable;

public class SlotItemHandler extends SlotExtended {

    private static final IInventory emptyInventory = new InventoryBasic("[Null]", true, 0);
    private final IItemHandler itemHandler;
    private final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (ItemHelpers.isEmpty(stack) || !this.itemHandler.isItemValid(this.index, stack)) {
            return false;
        }

        IItemHandler handler = this.getItemHandler();
        ItemStack remainder;
        if (handler instanceof IItemHandlerModifiable handlerModifiable) {
            ItemStack currentStack = handlerModifiable.getStackInSlot(this.index);
            handlerModifiable.setStackInSlot(this.index, ItemHelpers.EMPTY);
            remainder = handlerModifiable.insertItem(this.index, stack, true);
            handlerModifiable.setStackInSlot(this.index, currentStack);
        } else {
            remainder = handler.insertItem(this.index, stack, true);
        }

        int remainderSize = ItemHelpers.isEmpty(remainder) ? 0 : remainder.stackSize;
        return remainderSize < stack.stackSize;
    }

    @Override
    public ItemStack getStack() {
        return itemHandler.getStackInSlot(index);
    }

    @Override
    public void putStack(ItemStack stack) {
        if (this.getItemHandler() instanceof IItemHandlerModifiable handlerModifiable) {
            handlerModifiable.setStackInSlot(this.index, stack);
            this.onSlotChanged();
        }
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {}

    @Override
    public int getSlotStackLimit() {
        return itemHandler.getSlotLimit(index);
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (ItemHelpers.isEmpty(stack)) {
            return 0;
        }

        int maxInput = stack.getMaxStackSize();
        ItemStack maxAdd = ItemHelpers.copyWithSize(stack, maxInput);
        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(this.index);

        if (handler instanceof IItemHandlerModifiable handlerModifiable) {
            handlerModifiable.setStackInSlot(this.index, ItemHelpers.EMPTY);
            ItemStack remainder = handlerModifiable.insertItem(this.index, maxAdd, true);
            handlerModifiable.setStackInSlot(this.index, currentStack);

            int remainderSize = ItemHelpers.isEmpty(remainder) ? 0 : remainder.stackSize;
            return maxInput - remainderSize;
        } else {
            ItemStack remainder = handler.insertItem(this.index, maxAdd, true);
            int current = ItemHelpers.isEmpty(currentStack) ? 0 : currentStack.stackSize;
            int remainderSize = ItemHelpers.isEmpty(remainder) ? 0 : remainder.stackSize;
            int added = maxInput - remainderSize;
            return current + added;
        }
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return ItemHelpers.isEmpty(
            this.getItemHandler()
                .getStackInSlot(this.index))
            || !ItemHelpers.isEmpty(
                this.getItemHandler()
                    .extractItem(this.index, 1, true));
    }

    @Override
    @Nullable
    public ItemStack decrStackSize(int amount) {
        return getItemHandler().extractItem(this.index, amount, false);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }

    public boolean isSameInventory(Slot other) {
        return other instanceof SlotItemHandler slotHand && slotHand.getItemHandler() == this.itemHandler;
    }

    @Override
    public boolean isSlotInInventory(IInventory inventory, int invIndex) {
        return itemHandler.isSlotFromInventory(this.index, inventory, invIndex);
    }
}
