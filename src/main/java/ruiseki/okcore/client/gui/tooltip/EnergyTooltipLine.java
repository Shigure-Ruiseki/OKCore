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

    // ITooltipLine

    @Override
    public int getWidth(FontRenderer font) {
        if (this.barWidth == -1) {
            return -1;
        }

        return this.barWidth + 2;
    }

    @Override
    public int getHeight(FontRenderer font, int width) {
        return this.getTextHeight(font, width) + 2 + this.barHeight + 2;
    }

    @Override
    public void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height) {
        List<String> lines = this.getEnergyLines(font, width);

        int textHeight = lines.size() * font.FONT_HEIGHT;
        int textY = y;

        // Energy text.
        for (String line : lines) {
            font.drawStringWithShadow(line, x, textY, this.textColor);

            textY += font.FONT_HEIGHT;
        }

        // Resolve actual bar dimensions.
        int actualBarWidth = Math.max(0, width - 2);

        if (this.barWidth != -1) {
            actualBarWidth = Math.min(actualBarWidth, this.barWidth);
        }

        int actualBarHeight = Math.min(this.barHeight, Math.max(0, height - textHeight - 4));

        if (actualBarHeight <= 0 || width < 2) {
            return;
        }

        int barY = y + textHeight + 2;

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
