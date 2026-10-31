package ruiseki.okcore.client.mui.gui.component;

import net.minecraft.client.gui.FontRenderer;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;

import ruiseki.okcore.client.gui.tooltip.ITooltipLine;

public final class TooltipLineDrawable implements IDrawable {

    private static final int DEFAULT_WIDTH = 100;
    private final ITooltipLine line;
    private final FontRenderer font;
    private final int defaultWidth;

    public TooltipLineDrawable(ITooltipLine line, FontRenderer font, int maxWidth) {
        this.line = line;
        this.font = font;

        int preferredWidth = line.getWidth(font);
        int availableWidth = maxWidth > 0 ? Math.min(maxWidth, DEFAULT_WIDTH) : DEFAULT_WIDTH;

        this.defaultWidth = preferredWidth < 0 ? availableWidth : Math.min(preferredWidth, availableWidth);
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
