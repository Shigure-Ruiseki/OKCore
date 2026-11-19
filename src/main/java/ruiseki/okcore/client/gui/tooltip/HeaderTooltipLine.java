package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.GuiHelpers;

public class HeaderTooltipLine implements ITooltipLine {

    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXT_GAP = 4;
    private static final int VERTICAL_PADDING = 2;

    private final ItemStack stack;
    private final String title;

    public HeaderTooltipLine(ItemStack stack, String title) {
        this.stack = stack;
        this.title = title;
    }

    @Override
    public int getHeight(FontRenderer font, int width) {
        return ICON_SIZE + VERTICAL_PADDING * 2;
    }

    @Override
    public int getWidth(FontRenderer font) {
        return ICON_SIZE + ICON_TEXT_GAP + font.getStringWidth(title);
    }

    @Override
    public void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height) {
        GuiHelpers.drawItemStack(stack, x, y + VERTICAL_PADDING);
        int textX = x + ICON_SIZE + ICON_TEXT_GAP;
        int textY = y + (height - font.FONT_HEIGHT) / 2;
        font.drawStringWithShadow(title, textX, textY, -1);
    }
}
