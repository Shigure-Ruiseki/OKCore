package ruiseki.okcore.item.handler;

import net.minecraft.item.ItemStack;

public interface IItemHandlerStackLimit {

    int getStackLimit(int slot, ItemStack stack);
}
