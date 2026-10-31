package ruiseki.okcore.client.gui.tooltip;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import cofh.api.energy.IEnergyStorage;
import ruiseki.okcore.helper.LangHelpers;

public class EnergyTooltipLine implements ITooltipLine {

    private static final int DEFAULT_BAR_WIDTH = 100;
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
        this.barWidth = Math.max(1, width);
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

    private int getEnergyRatioWidth() {
        int max = getMaxEnergyStored();

        if (max <= 0) {
            return 0;
        }

        int stored = Math.min(getEnergyStored(), max);

        return (int) ((long) barWidth * stored / max);
    }

    // ITooltipLine

    @Override
    public int getWidth(FontRenderer font) {
        return Math.max(barWidth + 2, font.getStringWidth(getEnergyText()));
    }

    @Override
    public int getHeight(FontRenderer font) {
        return font.FONT_HEIGHT + 2 + barHeight + 2;
    }

    @Override
    public void draw(FontRenderer font, int x, int y, int mouseX, int mouseY, int width, int height) {
        // Energy text
        font.drawStringWithShadow(getEnergyText(), x, y, textColor);

        int barY = y + font.FONT_HEIGHT + 2;

        // Border
        Gui.drawRect(x, barY, x + barWidth + 2, barY + barHeight + 2, borderColor);

        // Background
        Gui.drawRect(x + 1, barY + 1, x + barWidth + 1, barY + barHeight + 1, backgroundColor);

        // Energy fill
        int fillWidth = getEnergyRatioWidth();

        if (fillWidth > 0) {
            Gui.drawRect(x + 1, barY + 1, x + 1 + fillWidth, barY + barHeight + 1, fillColor);
        }
    }
}
