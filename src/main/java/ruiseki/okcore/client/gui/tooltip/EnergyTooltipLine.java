
package ruiseki.okcore.client.gui.tooltip;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.StringHelpers;

public class EnergyTooltipLine implements ITooltipLine {

    private static final int DEFAULT_BAR_WIDTH = -1;
    private static final int DEFAULT_BAR_HEIGHT = 5;

    private static final int BAR_TOP_PADDING = 2;
    private static final int BAR_BOTTOM_PADDING = 2;
    private static final int BAR_BORDER_SIZE = 1;

    private static final int DEFAULT_BORDER_COLOR = 0xFF111111;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF404040;
    private static final int DEFAULT_FILL_COLOR = 0xFF35A853;
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;

    private final IEnergyStorage energyStorage;

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

    // Layout

    private List<String> getEnergyLines(FontRenderer font, int width) {
        return StringHelpers.splitLines(this.getEnergyText(), font, Math.max(1, width));
    }

    private int getTextHeight(FontRenderer font, int width) {
        return this.getEnergyLines(font, width)
            .size() * font.FONT_HEIGHT;
    }

    private int getTextWidth(FontRenderer font, int width) {
        int maxWidth = 0;
        for (String line : this.getEnergyLines(font, width)) {
            maxWidth = Math.max(maxWidth, font.getStringWidth(line));
        }
        return maxWidth;
    }

    private int getBarWidth(FontRenderer font, int width) {
        int availableWidth = Math.max(0, width - BAR_BORDER_SIZE * 2);
        if (this.barWidth != -1) {
            return Math.min(availableWidth, this.barWidth);
        }
        return Math.min(availableWidth, this.getTextWidth(font, width));
    }

    private int getRequiredHeight(FontRenderer font, int width) {
        int textHeight = this.getTextHeight(font, width);

        return textHeight + BAR_TOP_PADDING + this.barHeight + BAR_BORDER_SIZE * 2 + BAR_BOTTOM_PADDING;
    }

    // ITooltipLine

    @Override
    public int getWidth(FontRenderer font) {
        return this.barWidth == -1 ? -1 : this.barWidth + BAR_BORDER_SIZE * 2;
    }

    @Override
    public int getHeight(FontRenderer font, int width) {
        return Math.max(0, this.getRequiredHeight(font, width));
    }

    @Override
    public void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height) {
        width = Math.max(0, width);
        height = Math.max(0, height);

        if (width <= 0 || height <= 0) {
            return;
        }

        List<String> lines = this.getEnergyLines(font, width);

        int textHeight = lines.size() * font.FONT_HEIGHT;

        // Energy text.
        int textY = y;

        for (String line : lines) {
            font.drawStringWithShadow(line, x, textY, this.textColor);
            textY += font.FONT_HEIGHT;
        }

        // Energy bar.
        int actualBarWidth = this.getBarWidth(font, width);

        int barHeight = Math.min(
            this.barHeight,
            Math.max(0, height - textHeight - BAR_TOP_PADDING - BAR_BOTTOM_PADDING - BAR_BORDER_SIZE * 2));

        if (actualBarWidth <= 0 || barHeight <= 0) {
            return;
        }

        int barY = y + textHeight + BAR_TOP_PADDING;

        // Border.
        Gui.drawRect(
            x,
            barY,
            x + actualBarWidth + BAR_BORDER_SIZE * 2,
            barY + barHeight + BAR_BORDER_SIZE * 2,
            this.borderColor);

        // Background.
        Gui.drawRect(
            x + BAR_BORDER_SIZE,
            barY + BAR_BORDER_SIZE,
            x + BAR_BORDER_SIZE + actualBarWidth,
            barY + BAR_BORDER_SIZE + barHeight,
            this.backgroundColor);

        // Energy fill.
        int fillWidth = this.getEnergyRatioWidth(actualBarWidth);

        if (fillWidth > 0) {
            Gui.drawRect(
                x + BAR_BORDER_SIZE,
                barY + BAR_BORDER_SIZE,
                x + BAR_BORDER_SIZE + fillWidth,
                barY + BAR_BORDER_SIZE + barHeight,
                this.fillColor);
        }
    }
}
