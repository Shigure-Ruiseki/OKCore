package ruiseki.okcore.client.mui.gui.component;

import net.minecraft.client.gui.FontRenderer;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;

import ruiseki.okcore.client.gui.tooltip.ITooltipLine;

public final class TooltipLineDrawable implements IDrawable {

    private final ITooltipLine line;
    private final FontRenderer font;
    private final int defaultWidth;

    public TooltipLineDrawable(ITooltipLine line, FontRenderer font, int maxWidth, int fallbackWidth) {
        this.line = line;
        this.font = font;

        int preferredWidth = line.getWidth(font);

        if (preferredWidth < 0) {
            this.defaultWidth = maxWidth > 0 ? maxWidth : Math.max(1, fallbackWidth);
        } else {
            this.defaultWidth = maxWidth > 0 ? Math.min(preferredWidth, maxWidth) : preferredWidth;
        }
    }

    @Override
    public int getDefaultWidth() {
        return this.defaultWidth;
    }

    @Override
    public int getDefaultHeight() {
        return Math.max(0, this.line.getHeight(this.font, this.defaultWidth));
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        this.line.draw(this.font, x, y, context.getMouseX(), context.getMouseY(), width, height);
    }
}
