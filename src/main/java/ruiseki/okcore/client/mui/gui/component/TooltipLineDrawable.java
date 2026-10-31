package ruiseki.okcore.client.mui.gui.component;

import net.minecraft.client.gui.FontRenderer;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;

import ruiseki.okcore.client.gui.tooltip.ITooltipLine;

public final class TooltipLineDrawable implements IDrawable {

    private final ITooltipLine line;
    private final FontRenderer font;

    public TooltipLineDrawable(ITooltipLine line, FontRenderer font) {
        this.line = line;
        this.font = font;
    }

    @Override
    public int getDefaultWidth() {
        return Math.max(0, line.getWidth(font));
    }

    @Override
    public int getDefaultHeight() {
        return Math.max(0, line.getHeight(font));
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        line.draw(font, x, y, context.getMouseX(), context.getMouseY(), width, height);
    }
}
