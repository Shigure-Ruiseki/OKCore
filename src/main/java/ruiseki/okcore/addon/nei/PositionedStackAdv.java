package ruiseki.okcore.addon.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;

public class PositionedStackAdv extends PositionedStack {

    private final List<String> tooltip = new ArrayList<>();
    public float chance;
    public boolean showChance = true;
    public int textYOffset = 0;
    public float textScale = 0.8f;
    public int textColor = 0xFFFFFF;
    public String label = null;
    public int labelColor = 0x000000;

    public PositionedStackAdv(Object object, int x, int y) {
        super(object, x, y);
    }

    public PositionedStackAdv setTextYOffset(int offset) {
        this.textYOffset = offset;
        return this;
    }

    public PositionedStackAdv setTextColor(int color) {
        this.textColor = color;
        return this;
    }

    public PositionedStackAdv setTextScale(float scale) {
        this.textScale = scale;
        return this;
    }

    public PositionedStackAdv(Object object, int x, int y, List<String> tooltip) {
        super(object, x, y);
        this.addToTooltip(tooltip);
    }

    public Rectangle getRect() {
        return new Rectangle(this.relx - 1, this.rely - 1, 18, 18);
    }

    public List<String> handleTooltip(GuiRecipe<?> guiRecipe, List<String> currenttip) {
        if (!this.tooltip.isEmpty()) {
            for (String tip : this.tooltip) {
                currenttip.add(tip);
            }
        }
        return currenttip;
    }

    public PositionedStackAdv addToTooltip(List<String> lines) {
        for (String tip : lines) {
            this.tooltip.add(tip);
        }
        return this;
    }

    public PositionedStackAdv addToTooltip(String line) {
        this.tooltip.add(line);
        return this;
    }
    public void drawChance() {
        if (!showChance || chance > 1.0f || chance <= 0.0f) {
            return;
        }

        String text = (int) (chance * 100) + "%";

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int stringWidth = font.getStringWidth(text);
        float inverse = 1f / textScale;

        int x = this.relx + 1;
        int y = this.rely + 1;

        GL11.glPushMatrix();
        GL11.glScalef(textScale, textScale, 1.0f);
        font.drawString(
            text,
            (int) ((x + 8 - stringWidth * textScale / 2) * inverse),
            (int) ((y + 16 - font.FONT_HEIGHT * textScale + textYOffset) * inverse),
            textColor);
        GL11.glPopMatrix();
    }

    public void drawLabel() {
        if (label == null || label.isEmpty()) {
            return;
        }
        float scale = 0.8f;

        FontRenderer font = Minecraft.getMinecraft().fontRenderer;
        int stringWidth = font.getStringWidth(label);

        float inverse = 1f / scale;

        int x = this.relx + 1;
        int y = this.rely + 1;

        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1.0f);
        font.drawString(
            label,
            (int) ((x + 8 - stringWidth * scale / 2) * inverse),
            (int) ((y - 6) * inverse),
            labelColor);
        GL11.glPopMatrix();
    }

    public PositionedStackAdv setLabel(String label) {
        this.label = label;
        return this;
    }

    public PositionedStackAdv setLabelColor(int color) {
        this.labelColor = color;
        return this;
    }

    public PositionedStackAdv setChance(float chance) {
        this.chance = Math.max(0.0f, Math.min(1.0f, chance));
        return this;
    }
}
