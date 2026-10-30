package ruiseki.okcore.item.handler;

import net.minecraft.item.ItemStack;

public interface IItemHandlerSimpleInserter extends IItemHandlerModifiable {

    ItemStack insertItem(ItemStack stack, boolean simulate);
}
