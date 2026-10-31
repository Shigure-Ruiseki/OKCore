package ruiseki.okcore.item;

import net.minecraft.item.ItemStack;

public interface IItemTooltip {

    default int getMaxTooltipWidth(ItemStack stack) {
        return -1;
    }
}
