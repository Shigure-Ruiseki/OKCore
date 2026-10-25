package ruiseki.okcore.event.handler;

import net.minecraft.item.ItemStack;

import com.gtnewhorizon.gtnhlib.client.event.RenderTooltipEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.IItemTooltip;

public class TooltipEventHandler {

    public static final TooltipEventHandler INSTANCE = new TooltipEventHandler();

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderTooltip(RenderTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (ItemHelpers.isEmpty(stack)) return;
        if (!(stack.getItem() instanceof IItemTooltip tooltip)) return;
        event.alternativeRenderer = lines -> GuiHelpers.drawHoveringText(
            stack,
            lines,
            event.x,
            event.y,
            event.gui.width,
            event.gui.height,
            tooltip.getMaxTooltipWidth(stack),
            event.font);
    }
}
