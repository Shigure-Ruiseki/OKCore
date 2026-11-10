package ruiseki.okcore.item.handler;

import net.minecraft.item.ItemStack;

public interface IItemHandlerSimpleExtractor extends IItemHandlerModifiable {

    ItemStack extractItem(ItemStack stack, boolean simulate);
}
