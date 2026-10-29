package ruiseki.okcore.client.mui.gui.component.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.okcore.item.handler.IItemHandler;

public class ModularItemSlot extends ModularSlot {

    public ModularItemSlot(IItemHandler itemHandler, int index) {
        super(new ItemHandlerAdapter(itemHandler), index);
    }

    @Override
    public com.cleanroommc.modularui.utils.item.IItemHandler getItemHandler() {
        return super.getItemHandler();
    }

    @Override
    public ItemStack getStack() {
        return this.getItemHandler()
            .getStackInSlot(getSlotIndex());
    }

    @Override
    public int getSlotStackLimit() {
        return this.getItemHandler()
            .getSlotLimit(this.getSlotIndex());
    }

    @Override
    public boolean isSlotInInventory(IInventory inventory, int invIndex) {
        return this.getItemHandler()
            .isSlotFromInventory(getSlotIndex(), inventory, invIndex);
    }
}
