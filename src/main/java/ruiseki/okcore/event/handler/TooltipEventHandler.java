package ruiseki.okcore.event.handler;

import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.tooltip.HeaderTooltipLine;
import ruiseki.okcore.client.gui.tooltip.TooltipBuilder;
import ruiseki.okcore.client.gui.tooltip.TooltipLineHandler;
import ruiseki.okcore.event.gui.RenderTooltipEvent;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.IItemTooltip;

public class TooltipEventHandler {

    public static final TooltipEventHandler INSTANCE = new TooltipEventHandler();

    /**
     * Replaces the GTNHLib tooltip renderer with the OkCore tooltip renderer
     * for items implementing {@link IItemTooltip}.
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderTooltip(com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (ItemHelpers.isEmpty(stack)) return;
        if (!(stack.getItem() instanceof IItemTooltip)) return;

        event.alternativeRenderer = lines -> GuiHelpers
            .drawHoveringText(stack, lines, event.x, event.y, event.gui.width, event.gui.height, -1, event.font);
    }

    /**
     * Allows items implementing {@link IItemTooltip} to customize their
     * tooltip lines before tooltip layout and rendering.
     */
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPreRenderTooltip(RenderTooltipEvent.Pre event) {
        ItemStack stack = event.getStack();
        if (ItemHelpers.isEmpty(stack)) return;
        if (!(stack.getItem() instanceof IItemTooltip tooltip)) return;
        event.setMaxWidth(tooltip.getMaxTooltipWidth(stack, event.getLines(), event.getMaxWidth()));
        TooltipBuilder builder = tooltip.getTooltipBuilder();
        if (builder.isHeaderEnabled() && !event.getLines()
            .isEmpty()) {
            String title = event.getLines()
                .getFirst();
            HeaderTooltipLine header = new HeaderTooltipLine(stack, title);
            builder.applyHeader(header);
            event.getLines()
                .set(0, TooltipLineHandler.register(header));
        }
    }
}
