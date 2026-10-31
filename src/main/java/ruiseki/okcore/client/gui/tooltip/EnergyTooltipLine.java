package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.helper.LangHelpers;

public class EnergyTooltipLine implements ITooltipLine {

    /**
     * Use the allocated tooltip width.
     */
    private static final int DEFAULT_BAR_WIDTH = -1;
    private static final int DEFAULT_BAR_HEIGHT = 5;

    private static final int DEFAULT_BORDER_COLOR = 0xFF111111;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF404040;
    private static final int DEFAULT_FILL_COLOR = 0xFF35A853;
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;

    private final IEnergyStorage energyStorage;

    /**
     * -1 means automatic width.
     */
    private int barWidth = DEFAULT_BAR_WIDTH;
    private int barHeight = DEFAULT_BAR_HEIGHT;

    private int borderColor = DEFAULT_BORDER_COLOR;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int fillColor = DEFAULT_FILL_COLOR;
    private int textColor = DEFAULT_TEXT_COLOR;

    public EnergyTooltipLine(IEnergyStorage energyStorage) {
        this.energyStorage = energyStorage;
    }

    // Configuration

    public EnergyTooltipLine setWidth(int width) {
        this.barWidth = width == -1 ? -1 : Math.max(1, width);
        return this;
    }

    public EnergyTooltipLine setHeight(int height) {
        this.barHeight = Math.max(1, height);
        return this;
    }

    public EnergyTooltipLine setSize(int width, int height) {
        return this.setWidth(width)
            .setHeight(height);
    }

    public EnergyTooltipLine setBorderColor(int color) {
        this.borderColor = color;
        return this;
    }

    public EnergyTooltipLine setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public EnergyTooltipLine setFillColor(int color) {
        this.fillColor = color;
        return this;
    }

    public EnergyTooltipLine setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public EnergyTooltipLine setColors(int border, int background, int fill) {
        return this.setBorderColor(border)
            .setBackgroundColor(background)
            .setFillColor(fill);
    }

    // Energy

    private int getEnergyStored() {
        return Math.max(0, this.energyStorage.getEnergyStored());
    }

    private int getMaxEnergyStored() {
        return Math.max(0, this.energyStorage.getMaxEnergyStored());
    }

    private String getEnergyText() {
        return LangHelpers
            .localize("gui.okcore.tooltip.energy.tooltip_line", this.getEnergyStored(), this.getMaxEnergyStored());
    }

    private int getEnergyRatioWidth(int width) {
        int max = this.getMaxEnergyStored();

        if (max <= 0 || width <= 0) {
            return 0;
        }

        int stored = Math.min(this.getEnergyStored(), max);

        return (int) ((long) width * stored / max);
    }

    private String getVisibleEnergyText(FontRenderer font, int width) {
        String text = this.getEnergyText();

        if (width <= 0) {
            return "";
        }

        if (font.getStringWidth(text) <= width) {
            return text;
        }

        String ellipsis = "...";
        int ellipsisWidth = font.getStringWidth(ellipsis);

        if (width <= ellipsisWidth) {
            return font.trimStringToWidth(ellipsis, width);
        }

        return font.trimStringToWidth(text, width - ellipsisWidth) + ellipsis;
    }

    // ITooltipLine

    @Override
    public int getWidth(FontRenderer font) {
        if (this.barWidth == -1) {
            return -1;
        }

        return Math.max(this.barWidth + 2, font.getStringWidth(this.getEnergyText()));
    }

    @Override
    public int getHeight(FontRenderer font) {
        return font.FONT_HEIGHT + 2 + this.barHeight + 2;
    }

    @Override
    public void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height) {
        // Resolve actual bar dimensions.
        int actualBarWidth = this.barWidth == -1 ? Math.max(0, width - 2) : this.barWidth;

        int actualBarHeight = Math.min(this.barHeight, Math.max(1, height - font.FONT_HEIGHT - 4));

        // Energy text.
        font.drawStringWithShadow(this.getVisibleEnergyText(font, width), x, y, this.textColor);

        int barY = y + font.FONT_HEIGHT + 2;

        // Border.
        Gui.drawRect(x, barY, x + actualBarWidth + 2, barY + actualBarHeight + 2, this.borderColor);

        // Background.
        Gui.drawRect(x + 1, barY + 1, x + actualBarWidth + 1, barY + actualBarHeight + 1, this.backgroundColor);

        // Energy fill.
        int fillWidth = this.getEnergyRatioWidth(actualBarWidth);

        if (fillWidth > 0) {
            Gui.drawRect(x + 1, barY + 1, x + 1 + fillWidth, barY + actualBarHeight + 1, this.fillColor);
        }
    }
}
