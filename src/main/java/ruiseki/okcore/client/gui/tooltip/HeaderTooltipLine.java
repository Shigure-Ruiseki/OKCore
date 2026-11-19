package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.ItemHelpers;

public class HeaderTooltipLine implements ITooltipLine {

    private static final int ICON_SIZE = 16;
    private static final int ICON_TEXT_GAP = 4;
    private static final int VERTICAL_PADDING = 2;

    private ItemStack stack;
    private String title;
    private float scale = 1f;

    public HeaderTooltipLine(ItemStack stack, String title) {
        this.stack = ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
        this.title = title == null ? "" : title;
    }

    public HeaderTooltipLine scale(float scale) {
        if (!Float.isFinite(scale)) {
            throw new IllegalArgumentException("Scale must be finite");
        }
        this.scale = Math.max(0.1f, scale);
        return this;
    }

    public HeaderTooltipLine title(String title) {
        this.title = title == null ? "" : title;
        return this;
    }

    public HeaderTooltipLine stack(ItemStack stack) {
        this.stack = ItemHelpers.isEmpty(stack) ? ItemHelpers.EMPTY : stack;
        return this;
    }

    private int getScaledIconSize() {
        return Math.round(ICON_SIZE * this.scale);
    }

    @Override
    public int getHeight(FontRenderer font, int width) {
        return Math.max(font.FONT_HEIGHT, getScaledIconSize()) + VERTICAL_PADDING * 2;
    }

    @Override
    public int getWidth(FontRenderer font) {
        return getScaledIconSize() + ICON_TEXT_GAP + font.getStringWidth(this.title);
    }

    @Override
    public void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height) {
        GuiHelpers.drawScaledItemStack(this.stack, x, y + (height - getScaledIconSize()) / 2, this.scale);
        int textX = x + getScaledIconSize() + ICON_TEXT_GAP;
        int textY = y + (height - font.FONT_HEIGHT) / 2;
        font.drawStringWithShadow(this.title, textX, textY, 0xFFFFFF);
    }
}
