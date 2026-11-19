package ruiseki.okcore.client.gui.tooltip;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.GuiHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.helper.StringHelpers;

public class FluidTooltipLine implements ITooltipLine {

    private static final int DEFAULT_BAR_WIDTH = -1;
    private static final int DEFAULT_BAR_HEIGHT = 5;

    private static final int BAR_TOP_PADDING = 2;
    private static final int BAR_BOTTOM_PADDING = 2;
    private static final int BAR_BORDER_SIZE = 1;

    private static final int DEFAULT_BORDER_COLOR = 0xFF111111;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF404040;
    private static final int DEFAULT_FILL_COLOR = 0xFF35A853;
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;

    private final IFluidHandler fluidHandler;

    private int barWidth = DEFAULT_BAR_WIDTH;
    private int barHeight = DEFAULT_BAR_HEIGHT;

    private int borderColor = DEFAULT_BORDER_COLOR;
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;
    private int fillColor = DEFAULT_FILL_COLOR;
    private int textColor = DEFAULT_TEXT_COLOR;

    public FluidTooltipLine(IFluidHandler fluidHandler) {
        this.fluidHandler = fluidHandler;
    }

    // Configuration

    public FluidTooltipLine setWidth(int width) {
        this.barWidth = width == -1 ? -1 : Math.max(1, width);
        return this;
    }

    public FluidTooltipLine setHeight(int height) {
        this.barHeight = Math.max(1, height);
        return this;
    }

    public FluidTooltipLine setSize(int width, int height) {
        return this.setWidth(width)
            .setHeight(height);
    }

    public FluidTooltipLine setBorderColor(int color) {
        this.borderColor = color;
        return this;
    }

    public FluidTooltipLine setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public FluidTooltipLine setFillColor(int color) {
        this.fillColor = color;
        return this;
    }

    public FluidTooltipLine setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public FluidTooltipLine setColors(int border, int background, int fill) {
        return this.setBorderColor(border)
            .setBackgroundColor(background)
            .setFillColor(fill);
    }

    // Fluid

    private int getFluidAmount(int tank) {
        FluidStack fluid = this.fluidHandler.getFluidInTank(tank);
        return FluidHelpers.isEmpty(fluid) ? 0 : Math.max(0, fluid.amount);
    }

    private int getFluidCapacity(int tank) {
        return Math.max(0, this.fluidHandler.getTankCapacity(tank));
    }

    private FluidStack getFluid(int tank) {
        FluidStack fluid = this.fluidHandler.getFluidInTank(tank);
        return FluidHelpers.isEmpty(fluid) ? null : fluid;
    }

    private int getFluidRatioWidth(int tank, int width) {
        int capacity = this.getFluidCapacity(tank);
        if (capacity <= 0 || width <= 0) return 0;
        int amount = Math.min(this.getFluidAmount(tank), capacity);
        return (int) ((long) width * amount / capacity);
    }

    private String getFluidText(int tank) {
        FluidStack stack = this.getFluid(tank);
        String fluidName = stack == null ? LangHelpers.localize("gui.okcore.tooltip.empty") : stack.getLocalizedName();
        return LangHelpers.localize(
            "gui.okcore.tooltip.tooltip_line.fluid",
            fluidName,
            this.getFluidAmount(tank),
            this.getFluidCapacity(tank));
    }

    // Layout

    private List<String> getFluidLines(FontRenderer font, int tank, int width) {
        return StringHelpers.splitLines(this.getFluidText(tank), font, Math.max(1, width));
    }

    private int getTextHeight(FontRenderer font, int width) {
        int height = 0;

        for (int tank = 0; tank < this.fluidHandler.getTanks(); tank++) {
            height += this.getFluidLines(font, tank, width)
                .size() * font.FONT_HEIGHT;
        }

        return height;
    }

    private int getTextWidth(FontRenderer font, int width) {
        int maxWidth = 0;

        for (int tank = 0; tank < this.fluidHandler.getTanks(); tank++) {
            for (String line : this.getFluidLines(font, tank, width)) {
                maxWidth = Math.max(maxWidth, font.getStringWidth(line));
            }
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
        int tanks = Math.max(0, this.fluidHandler.getTanks());

        if (tanks == 0) {
            return 0;
        }

        return this.getTextHeight(font, width)
            + tanks * (BAR_TOP_PADDING + this.barHeight + BAR_BORDER_SIZE * 2 + BAR_BOTTOM_PADDING);
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

        int currentY = y;
        int actualBarWidth = this.getBarWidth(font, width);

        for (int tank = 0; tank < this.fluidHandler.getTanks(); tank++) {
            List<String> lines = this.getFluidLines(font, tank, width);
            int textHeight = lines.size() * font.FONT_HEIGHT;

            // Fluid text.
            int textY = currentY;

            for (String line : lines) {
                font.drawStringWithShadow(line, x, textY, this.textColor);
                textY += font.FONT_HEIGHT;
            }

            // Fluid bar.
            int barHeight = this.barHeight;
            int barY = currentY + textHeight + BAR_TOP_PADDING;

            int requiredBarHeight = BAR_TOP_PADDING + barHeight + BAR_BORDER_SIZE * 2 + BAR_BOTTOM_PADDING;

            if (currentY + textHeight + requiredBarHeight > y + height) {
                return;
            }

            if (actualBarWidth > 0) {
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

                // Fluid texture.
                int fillWidth = this.getFluidRatioWidth(tank, actualBarWidth);

                if (fillWidth > 0) {
                    GuiHelpers.drawFluidTexture(
                        this.getFluid(tank),
                        x + BAR_BORDER_SIZE,
                        barY + BAR_BORDER_SIZE,
                        fillWidth,
                        barHeight);
                }
            }

            currentY += textHeight + requiredBarHeight;
        }
    }
}
