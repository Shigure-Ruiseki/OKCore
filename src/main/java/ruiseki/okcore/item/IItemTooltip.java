package ruiseki.okcore.item;

import java.util.function.Consumer;

import net.minecraft.item.ItemStack;

import ruiseki.okcore.client.gui.tooltip.TooltipBuilder;

public interface IItemTooltip {

    default int getMaxTooltipWidth(ItemStack stack) {
        return -1;
    }

    default <T extends IItemTooltip> T tooltipBuilder(Consumer<TooltipBuilder> consumer) {
        consumer.accept(getTooltipBuilder());
        @SuppressWarnings("unchecked")
        T self = (T) this;
        return self;
    }

    TooltipBuilder getTooltipBuilder();
}
