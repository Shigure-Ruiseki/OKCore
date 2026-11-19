package ruiseki.okcore.event.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.drawable.IRichTextBuilder;
import com.cleanroommc.modularui.drawable.text.RichText;
import com.cleanroommc.modularui.screen.RichTooltipEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.gui.tooltip.ITooltipLine;
import ruiseki.okcore.client.gui.tooltip.TooltipLineHandler;
import ruiseki.okcore.client.mui.gui.component.TooltipLineText;
import ruiseki.okcore.event.gui.RenderTooltipEvent;
import ruiseki.okcore.helper.ItemHelpers;

public class MuiTooltipEventHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderPre(RichTooltipEvent.Pre event) {
        ItemStack stack = event.getItemStack();
        if (ItemHelpers.isEmpty(stack)) return;

        IRichTextBuilder<?> builder = event.getTooltip();
        if (builder == null) return;

        FontRenderer font = getFont();
        RenderTooltipEvent.Pre forwarded = new RenderTooltipEvent.Pre(
            stack,
            getLines(builder),
            event.getX(),
            event.getY(),
            event.getScreenWidth(),
            event.getScreenHeight(),
            event.getMaxWidth(),
            font);

        if (MinecraftForge.EVENT_BUS.post(forwarded)) {
            event.setCanceled(true);
            TooltipLineHandler.clear();
            return;
        }

        event.setX(forwarded.getX());
        event.setY(forwarded.getY());
        event.setScreenWidth(forwarded.getScreenWidth());
        event.setScreenHeight(forwarded.getScreenHeight());
        event.setMaxWidth(forwarded.getMaxWidth());
        event.setFontRenderer(forwarded.getFontRenderer());

        FontRenderer renderFont = forwarded.getFontRenderer();

        builder.moveCursorToStart();
        for (String text : new ArrayList<>(getLines(event.getTooltip()))) {
            if (text == null) continue;

            Pattern marker = Pattern.compile(Pattern.quote(text));

            ITooltipLine handler = TooltipLineHandler.getTipLine(text);
            if (handler != null) {
                builder.replace(marker, key -> null);
                builder.moveCursorForward(1);
                builder.addLine(new TooltipLineText(handler, renderFont, forwarded.getMaxWidth()));
                continue;
            }

            if (text.contains(TooltipLineHandler.TOOLTIP_LINESPACE)) {
                boolean standalone = text.equals(TooltipLineHandler.TOOLTIP_LINESPACE);
                if (standalone) {
                    builder.replace(marker, key -> null);
                } else {
                    builder.replace(marker, key -> IKey.str(text.replace(TooltipLineHandler.TOOLTIP_LINESPACE, "")));
                }
                builder.spaceLine(2);
            }
        }

        builder.moveCursorToEnd();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderColor(RichTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();
        if (ItemHelpers.isEmpty(stack)) return;

        RenderTooltipEvent.Color forwarded = new RenderTooltipEvent.Color(
            stack,
            getLines(event.getTooltip()),
            event.getX(),
            event.getY(),
            getFont(),
            event.getBackground(),
            event.getBorderStart(),
            event.getBorderEnd());

        MinecraftForge.EVENT_BUS.post(forwarded);

        event.setBackground(forwarded.getBackground());
        event.setBorderStart(forwarded.getBorderStart());
        event.setBorderEnd(forwarded.getBorderEnd());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderPostBackground(RichTooltipEvent.PostBackground event) {
        ItemStack stack = event.getItemStack();
        if (ItemHelpers.isEmpty(stack)) return;

        RenderTooltipEvent.PostBackground forwarded = new RenderTooltipEvent.PostBackground(
            stack,
            getLines(event.getTooltip()),
            event.getX(),
            event.getY(),
            getFont(),
            event.getWidth(),
            event.getHeight());

        MinecraftForge.EVENT_BUS.post(forwarded);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderPostText(RichTooltipEvent.PostText event) {
        try {
            ItemStack stack = event.getItemStack();
            if (ItemHelpers.isEmpty(stack)) return;

            RenderTooltipEvent.PostText forwarded = new RenderTooltipEvent.PostText(
                stack,
                getLines(event.getTooltip()),
                event.getX(),
                event.getY(),
                getFont(),
                event.getWidth(),
                event.getHeight());

            MinecraftForge.EVENT_BUS.post(forwarded);
        } finally {
            TooltipLineHandler.clear();
        }
    }

    @SideOnly(Side.CLIENT)
    private static List<String> getLines(IRichTextBuilder<?> builder) {
        if (builder == null) return Collections.emptyList();
        if (builder.getRichText() instanceof RichText richText) {
            return richText.getAsStrings();
        }
        return Collections.emptyList();
    }

    @SideOnly(Side.CLIENT)
    private static FontRenderer getFont() {
        return Minecraft.getMinecraft().fontRenderer;
    }
}
