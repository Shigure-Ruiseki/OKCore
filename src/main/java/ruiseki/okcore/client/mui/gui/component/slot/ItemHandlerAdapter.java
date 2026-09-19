package ruiseki.okcore.client.mui.gui.component.slot;

import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import com.cleanroommc.modularui.utils.item.IItemHandlerModifiable;

public class ItemHandlerAdapter implements IItemHandlerModifiable, ruiseki.okcore.item.handler.IItemHandlerModifiable {

    private final ruiseki.okcore.item.handler.IItemHandler internalHandler;

    public ItemHandlerAdapter(ruiseki.okcore.item.handler.IItemHandler internalHandler) {
        this.internalHandler = internalHandler;
    }

    @Override
    public int getSlots() {
        return internalHandler.getSlots();
    }

    @Override
    @Nullable
    public ItemStack getStackInSlot(int slot) {
        return internalHandler.getStackInSlot(slot);
    }

    @Override
    @Nullable
    public ItemStack insertItem(int slot, @Nullable ItemStack stack, boolean simulate) {
        return internalHandler.insertItem(slot, stack, simulate);
    }

    @Override
    @Nullable
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return internalHandler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return internalHandler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nullable ItemStack stack) {
        return internalHandler.isItemValid(slot, stack);
    }

    @Override
    public List<ItemStack> getStacks() {
        return internalHandler.getStacks();
    }

    @Override
    public boolean isSlotFromInventory(int index, IInventory inventory, int invIndex) {
        return internalHandler.isSlotFromInventory(index, inventory, invIndex);
    }

    @Override
    public void setStackInSlot(int slot, @Nullable ItemStack stack) {
        if (internalHandler instanceof ruiseki.okcore.item.handler.IItemHandlerModifiable modifiable) {
            modifiable.setStackInSlot(slot, stack);
        } else {
            throw new RuntimeException("Not modifiable!");
        }
    }
}
