package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;

public interface ITooltipLine {

    int getWidth(FontRenderer font);

    int getHeight(FontRenderer font);

    void draw(FontRenderer font, int x, int y);
}
