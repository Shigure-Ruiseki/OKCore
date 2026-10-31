
package ruiseki.okcore.client.mui.gui.component;

import net.minecraft.client.gui.FontRenderer;

import com.cleanroommc.modularui.api.drawable.ITextLine;
import com.cleanroommc.modularui.screen.viewport.GuiContext;

import ruiseki.okcore.client.gui.tooltip.ITooltipLine;

public class TooltipLineText implements ITextLine {

    private static final int DEFAULT_WIDTH = 100;

    private final ITooltipLine line;
    private final FontRenderer font;
    private final int width;
    private final int height;

    public TooltipLineText(ITooltipLine line, FontRenderer font, int maxWidth) {
        this.line = line;
        this.font = font;
        this.width = maxWidth > 0 ? maxWidth : DEFAULT_WIDTH;
        this.height = Math.max(0, line.getHeight(font, this.width));
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight(FontRenderer font) {
        return this.height;
    }

    @Override
    public void draw(GuiContext context, FontRenderer font, float x, float y, int color, boolean shadow,
        int availableWidth, int availableHeight) {
        this.line.draw(this.font, (int) x, (int) y, context.getMouseX(), context.getMouseY(), this.width, this.height);
    }

    @Override
    public Object getHoveringElement(FontRenderer font, int mouseX, int mouseY) {
        return null;
    }
}
