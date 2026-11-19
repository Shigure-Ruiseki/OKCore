
package ruiseki.okcore.item;

import java.util.List;
import java.util.function.Consumer;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.client.gui.tooltip.HeaderTooltipLine;
import ruiseki.okcore.client.gui.tooltip.TooltipBuilder;
import ruiseki.okcore.client.gui.tooltip.TooltipLineHandler;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Provides customization hooks for an item's tooltip.
 *
 * <p>
 * Items implementing this interface can customize tooltip layout,
 * modify tooltip lines before rendering, and configure additional
 * tooltip content through {@link TooltipBuilder}.
 * </p>
 *
 * <p>
 * The tooltip customization hooks are invoked on the client side.
 * </p>
 */
public interface IItemTooltip {

    /**
     * Returns the maximum width allowed for this item's tooltip.
     *
     * <p>
     * Override this method to specify a custom maximum width.
     * Returning {@code -1} uses the default width determined by the
     * tooltip renderer.
     * </p>
     *
     * @param stack the item stack being displayed
     * @param list  the current tooltip lines
     * @return the maximum tooltip width, or {@code -1} for the default
     */
    default int getMaxTooltipWidth(ItemStack stack, List<String> list) {
        return -1;
    }

    /**
     * Modifies the tooltip lines before they are measured and rendered.
     *
     * <p>
     * By default, the first line is replaced with a registered
     * {@link HeaderTooltipLine}, which displays the item icon alongside
     * its original title. Optional localized information is then appended.
     * </p>
     *
     * <p>
     * Override this method to customize the tooltip header or add,
     * remove, and modify tooltip lines before rendering.
     * </p>
     *
     * @param stack the item stack being displayed
     * @param list  the mutable tooltip lines
     */
    default void onPreRenderTooltip(ItemStack stack, List<String> list) {
        if (list.isEmpty()) return;
        list.set(0, TooltipLineHandler.register(new HeaderTooltipLine(stack, list.getFirst())));
        LangHelpers.addOptionalInfo(list, ((Item) this).getUnlocalizedName(stack));
    }

    /**
     * Configures this item's {@link TooltipBuilder}.
     *
     * <p>
     * The supplied consumer is applied immediately to the builder
     * returned by {@link #getTooltipBuilder()}.
     * </p>
     *
     * @param consumer the configuration to apply to the tooltip builder
     * @param <T>      the concrete item tooltip type
     * @return this instance for method chaining
     */
    default <T extends IItemTooltip> T tooltipBuilder(Consumer<TooltipBuilder> consumer) {
        consumer.accept(getTooltipBuilder());
        @SuppressWarnings("unchecked")
        T self = (T) this;
        return self;
    }

    /**
     * Returns the tooltip builder used to configure this item's additional
     * tooltip content.
     *
     * @return this item's tooltip builder
     */
    TooltipBuilder getTooltipBuilder();
}
