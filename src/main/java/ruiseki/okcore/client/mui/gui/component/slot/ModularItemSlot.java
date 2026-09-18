package ruiseki.okcore.client.mui.gui.component.slot;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.okcore.item.handler.IItemHandler;

public class ModularItemSlot extends ModularSlot {

    /**
     * Creates a ModularSlot
     *
     * @param itemHandler item handler of the slot
     * @param index       slot index in the item handler
     */
    public ModularItemSlot(IItemHandler itemHandler, int index) {
        super(new ItemHandlerAdapter(itemHandler), index);
    }
}
