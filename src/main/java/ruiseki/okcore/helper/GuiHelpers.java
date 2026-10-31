package ruiseki.okcore.helper;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL12.GL_RESCALE_NORMAL;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import cpw.mods.fml.relauncher.ReflectionHelper;
import ruiseki.okcore.client.gui.tooltip.ITooltipLine;
import ruiseki.okcore.client.gui.tooltip.TooltipLineHandler;
import ruiseki.okcore.client.renderer.GlStateManager;
import ruiseki.okcore.event.gui.RenderTooltipEvent;

public class GuiHelpers {

    private static final RenderItem render = RenderItem.getInstance();

    /**
     * The default item slot size. Width and height are equal.
     */
    public static int SLOT_SIZE = 18;
    /**
     * The default inner item slot size. Width and height are equal.
     */
    public static int SLOT_SIZE_INNER = 16;

    /**
     * @param mouseX - Position of the mouse on the x-axiq
     * @param mouseY - Position of the mouse on the y-axis
     * @param x      - Starting x for the rectangle
     * @param y      - Starting y for the rectangle
     * @param width  - Width of the rectangle
     * @param height - Height of the rectangle
     * @return whether or not the mouse is in the rectangle
     */
    public static boolean isMouseBetween(int mouseX, int mouseY, int x, int y, int width, int height) {
        int xSize = x + width;
        int ySize = y + height;
        return (mouseX >= x && mouseX <= xSize && mouseY >= y && mouseY <= ySize);
    }

    /**
     * @param stack - The itemstack to be drawn
     * @param x     - The position on the x-axis to draw the itemstack
     * @param y     - The position on the y-axis to draw the itemstack
     */
    public static void drawItemStack(ItemStack stack, int x, int y) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        glEnable(GL_RESCALE_NORMAL);
        glEnable(GL_DEPTH_TEST);
        render.renderItemAndEffectIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            x,
            y);
        render.renderItemOverlayIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            x,
            y);
        RenderHelper.disableStandardItemLighting();
        glPopMatrix();
        glDisable(GL_LIGHTING);
    }

    /**
     * @param stack - The itemstack to be drawn
     * @param x     - The position on the x-axis to draw the itemstack
     * @param y     - The position on the y-axis to draw the itemstack
     * @param scale - The scale with which to draw the itemstack
     */
    public static void drawScaledItemStack(ItemStack stack, int x, int y, float scale) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glScalef(scale, scale, 1.0F);
        RenderHelper.enableGUIStandardItemLighting();
        glEnable(GL_RESCALE_NORMAL);
        glEnable(GL_DEPTH_TEST);
        render.renderItemAndEffectIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            (int) (x / scale),
            (int) (y / scale));
        render.renderItemOverlayIntoGUI(
            Minecraft.getMinecraft().fontRenderer,
            Minecraft.getMinecraft()
                .getTextureManager(),
            stack,
            x,
            y);
        RenderHelper.disableStandardItemLighting();
        glPopMatrix();
    }

    /**
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param zLevel -
     */
    public static void drawIconWithoutColor(int x, int y, int width, int height, float zLevel) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        glEnable(GL_RESCALE_NORMAL);
        glEnable(GL_DEPTH_TEST);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x + 0, y + height, zLevel, 0D, 1D);
        t.addVertexWithUV(x + width, y + height, zLevel, 1D, 1D);
        t.addVertexWithUV(x + width, y + 0, zLevel, 1D, 0D);
        t.addVertexWithUV(x + 0, y + 0, zLevel, 0D, 0D);
        t.draw();
        RenderHelper.disableStandardItemLighting();
        glDisable(GL_LIGHTING);
        glPopMatrix();
    }

    /**
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param zLevel -
     * @param color  - The color the icon will have
     */
    public static void drawIconWithColor(int x, int y, int width, int height, float zLevel, Color color) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        RenderHelper.enableGUIStandardItemLighting();
        glEnable(GL_RESCALE_NORMAL);
        glEnable(GL_DEPTH_TEST);
        glColor4f(
            (float) color.getRed() / 255F,
            (float) color.getGreen() / 255F,
            (float) color.getBlue() / 255F,
            (float) color.getAlpha() / 255F);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x + 0, y + height, zLevel, 0D, 1D);
        t.addVertexWithUV(x + width, y + height, zLevel, 1D, 1D);
        t.addVertexWithUV(x + width, y + 0, zLevel, 1D, 0D);
        t.addVertexWithUV(x + 0, y + 0, zLevel, 0D, 0D);
        t.draw();
        RenderHelper.disableStandardItemLighting();
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glDisable(GL_LIGHTING);
        glPopMatrix();
    }

    /**
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param zLevel -
     */
    public static void drawSizedIconWithoutColor(int x, int y, int width, int height, float zLevel) {
        glPushMatrix();
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL11.GL_LIGHTING);
        glEnable(GL11.GL_ALPHA_TEST);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator t = TessellatorManager.get();
        t.startDrawingQuads();
        t.addVertexWithUV(x, y + height, zLevel, 0.0D, 1.0D);
        t.addVertexWithUV(x + width, y + height, zLevel, 1.0D, 1.0D);
        t.addVertexWithUV(x + width, y, zLevel, 1.0D, 0.0D);
        t.addVertexWithUV(x, y, zLevel, 0.0D, 0.0D);
        t.draw();
        glDisable(GL11.GL_BLEND);
        glPopMatrix();
    }

    /**
     * @param x      - The position on the x-axis to draw the icon
     * @param y      - The position on the y-axis to draw the icon
     * @param width  - The width of the icon
     * @param height - The height of the icon
     * @param zLevel
     * @param color  - The color the icon will have
     */
    public static void drawSizedIconWithColor(int x, int y, int width, int height, float zLevel, Color color) {
        glPushMatrix();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glScaled(0.5D, 0.5D, 0.5D);
        glColor4f(
            (float) color.getRed() / 255F,
            (float) color.getGreen() / 255F,
            (float) color.getBlue() / 255F,
            (float) color.getAlpha() / 255F);
        glTranslated(x, y, zLevel);
        RenderHelper.enableGUIStandardItemLighting();
        glEnable(GL_RESCALE_NORMAL);
        glEnable(GL_DEPTH_TEST);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.addVertexWithUV(x + 0, y + height, zLevel, 0D, 1D);
        t.addVertexWithUV(x + width, y + height, zLevel, 1D, 1D);
        t.addVertexWithUV(x + width, y + 0, zLevel, 1D, 0D);
        t.addVertexWithUV(x + 0, y + 0, zLevel, 0D, 0D);
        t.draw();
        RenderHelper.disableStandardItemLighting();
        glDisable(GL_LIGHTING);
        glPopMatrix();
    }

    @SuppressWarnings("unchecked")
    public static List<String> getTooltip(ItemStack stack) {
        Minecraft mc = Minecraft.getMinecraft();
        List<String> list = stack.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips);

        for (int k = 0; k < list.size(); ++k) {
            if (k == 0) {
                list.set(k, stack.getRarity().rarityColor + (String) list.get(k));
            } else {
                list.set(k, EnumChatFormatting.GRAY + (String) list.get(k));
            }
        }

        return list;
    }

    @Nonnull
    private static ItemStack cachedTooltipStack = null;

    /**
     * Must be called from {@code GuiScreen.renderToolTip} before {@code GuiScreen.drawHoveringText} is called.
     *
     * @param stack The stack for which a tooltip is about to be drawn.
     */
    public static void preItemToolTip(@Nonnull ItemStack stack) {
        cachedTooltipStack = stack;
    }

    /**
     * Must be called from {@code GuiScreen.renderToolTip} after {@code GuiScreen.drawHoveringText} is called.
     */
    public static void postItemToolTip() {
        cachedTooltipStack = null;
    }

    /**
     * Draws a tooltip box on the screen with text in it.
     * Automatically positions the box relative to the mouse to match Mojang's implementation.
     * Automatically wraps text when there is not enough space on the screen to display the text without wrapping.
     * Can have a maximum width set to avoid creating very wide tooltips.
     *
     * @param textLines    the lines of text to be drawn in a hovering tooltip box.
     * @param mouseX       the mouse X position
     * @param mouseY       the mouse Y position
     * @param screenWidth  the available screen width for the tooltip to drawn in
     * @param screenHeight the available screen height for the tooltip to drawn in
     * @param maxTextWidth the maximum width of the text in the tooltip box.
     *                     Set to a negative number to have no max width.
     * @param font         the font for drawing the text in the tooltip box
     */
    public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth,
        int screenHeight, int maxTextWidth, FontRenderer font) {
        drawHoveringText(cachedTooltipStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
    }

    /**
     * Draws a tooltip with text and optional custom tooltip lines.
     */

    public static void drawHoveringText(ItemStack stack, List<String> textLines, int mouseX, int mouseY,
        int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (textLines != null && !textLines.isEmpty()) {
            RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(
                stack,
                textLines,
                mouseX,
                mouseY,
                screenWidth,
                screenHeight,
                maxTextWidth,
                font);
            if (MinecraftForge.EVENT_BUS.post(event)) {
                TooltipLineHandler.clear();
            } else {
                mouseX = event.getX();
                mouseY = event.getY();
                screenWidth = event.getScreenWidth();
                screenHeight = event.getScreenHeight();
                maxTextWidth = event.getMaxWidth();
                font = event.getFontRenderer();
                GlStateManager.disableRescaleNormal();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                int tooltipTextWidth = 0;
                boolean hasFlexibleWidth = false;

                for (String textLine : textLines) {
                    ITooltipLine handler = TooltipLineHandler.getTipLine(textLine);
                    if (handler != null) {
                        int width = handler.getWidth(font);
                        if (width < 0) {
                            hasFlexibleWidth = true;
                        } else {
                            tooltipTextWidth = Math.max(tooltipTextWidth, width);
                        }
                    } else {
                        int width = font.getStringWidth(textLine.replace(TooltipLineHandler.TOOLTIP_LINESPACE, ""));
                        tooltipTextWidth = Math.max(tooltipTextWidth, width);
                    }
                }

                if (tooltipTextWidth <= 0 && hasFlexibleWidth) {
                    int availableWidth = mouseX > screenWidth / 2 ? mouseX - 20 : screenWidth - 16 - mouseX;
                    if (maxTextWidth > 0) {
                        availableWidth = Math.min(availableWidth, maxTextWidth);
                    }

                    tooltipTextWidth = Math.max(1, availableWidth);
                }

                boolean needsWrap = false;
                int titleLinesCount = 1;
                int tooltipX = mouseX + 12;
                if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                    if (tooltipX < 4) {
                        if (mouseX > screenWidth / 2) {
                            tooltipTextWidth = mouseX - 20;
                        } else {
                            tooltipTextWidth = screenWidth - 16 - mouseX;
                        }

                        needsWrap = true;
                    }
                }

                if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
                    tooltipTextWidth = maxTextWidth;
                    needsWrap = true;
                }

                if (needsWrap) {
                    int wrappedTooltipWidth = 0;
                    List<String> wrappedTextLines = new ArrayList<>();
                    tooltipTextWidth = Math.max(1, tooltipTextWidth);

                    for (int i = 0; i < textLines.size(); i++) {
                        String textLine = textLines.get(i);
                        ITooltipLine handler = TooltipLineHandler.getTipLine(textLine);
                        if (handler != null) {
                            wrappedTextLines.add(textLine);
                            int width = handler.getWidth(font);
                            if (width >= 0) {
                                wrappedTooltipWidth = Math.max(wrappedTooltipWidth, Math.min(width, tooltipTextWidth));
                            }
                        } else {
                            boolean hasSpacing = textLine.contains(TooltipLineHandler.TOOLTIP_LINESPACE);
                            String cleanText = textLine.replace(TooltipLineHandler.TOOLTIP_LINESPACE, "");
                            List<String> wrappedLine = font.listFormattedStringToWidth(cleanText, tooltipTextWidth);
                            if (i == 0) {
                                titleLinesCount = wrappedLine.size();
                            }

                            for (int j = 0; j < wrappedLine.size(); j++) {
                                String line = wrappedLine.get(j);
                                wrappedTooltipWidth = Math.max(wrappedTooltipWidth, font.getStringWidth(line));
                                if (hasSpacing && j == wrappedLine.size() - 1) {
                                    line += TooltipLineHandler.TOOLTIP_LINESPACE;
                                }

                                wrappedTextLines.add(line);
                            }
                        }
                    }

                    if (wrappedTooltipWidth <= 0 && hasFlexibleWidth) {
                        wrappedTooltipWidth = tooltipTextWidth;
                    }

                    tooltipTextWidth = Math.max(1, wrappedTooltipWidth);
                    textLines = wrappedTextLines;
                }

                if (mouseX + 12 + tooltipTextWidth + 4 <= screenWidth) {
                    tooltipX = mouseX + 12;
                } else if (mouseX - 16 - tooltipTextWidth >= 4) {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = Math.max(4, screenWidth - tooltipTextWidth - 4);
                }

                int tooltipTextHeight = 0;

                for (int i = 0; i < textLines.size(); ++i) {
                    String textLine = textLines.get(i);
                    ITooltipLine handler = TooltipLineHandler.getTipLine(textLine);
                    if (handler != null) {
                        int width = getTooltipLineWidth(handler, font, tooltipTextWidth);
                        tooltipTextHeight += Math.max(0, handler.getHeight(font, width));
                    } else {
                        tooltipTextHeight += 10;
                    }

                    if (i + 1 == titleLinesCount && i + 1 < textLines.size()) {
                        tooltipTextHeight += 2;
                    }

                    if (i + 1 < textLines.size() && textLine.contains(TooltipLineHandler.TOOLTIP_LINESPACE)) {
                        tooltipTextHeight += 2;
                    }
                }

                if (!textLines.isEmpty() && TooltipLineHandler.getTipLine(textLines.getLast()) == null) {
                    tooltipTextHeight -= 2;
                }

                int tooltipY = mouseY - 12;
                if (tooltipY < 4) {
                    tooltipY = 4;
                } else if (tooltipY + tooltipTextHeight + 4 > screenHeight) {
                    tooltipY = screenHeight - tooltipTextHeight - 4;
                }

                tooltipY = Math.max(4, tooltipY);
                int backgroundColor = 0xF0100010;
                int borderColorStart = 0x505000FF;
                int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
                RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(
                    stack,
                    textLines,
                    tooltipX,
                    tooltipY,
                    font,
                    backgroundColor,
                    borderColorStart,
                    borderColorEnd);
                MinecraftForge.EVENT_BUS.post(colorEvent);
                backgroundColor = colorEvent.getBackground();
                borderColorStart = colorEvent.getBorderStart();
                borderColorEnd = colorEvent.getBorderEnd();
                drawGradientRect(
                    tooltipX - 3,
                    tooltipY - 4,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3,
                    backgroundColor,
                    backgroundColor,
                    300);
                drawGradientRect(
                    tooltipX - 3,
                    tooltipY + tooltipTextHeight + 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipTextHeight + 4,
                    backgroundColor,
                    backgroundColor,
                    300);
                drawGradientRect(
                    tooltipX - 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipTextHeight + 3,
                    backgroundColor,
                    backgroundColor,
                    300);
                drawGradientRect(
                    tooltipX - 4,
                    tooltipY - 3,
                    tooltipX - 3,
                    tooltipY + tooltipTextHeight + 3,
                    backgroundColor,
                    backgroundColor,
                    300);
                drawGradientRect(
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 4,
                    tooltipY + tooltipTextHeight + 3,
                    backgroundColor,
                    backgroundColor,
                    300);
                drawGradientRect(
                    tooltipX - 3,
                    tooltipY - 2,
                    tooltipX - 2,
                    tooltipY + tooltipTextHeight + 2,
                    borderColorStart,
                    borderColorEnd,
                    300);
                drawGradientRect(
                    tooltipX + tooltipTextWidth + 2,
                    tooltipY - 2,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipTextHeight + 2,
                    borderColorStart,
                    borderColorEnd,
                    300);
                drawGradientRect(
                    tooltipX - 3,
                    tooltipY - 3,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY - 2,
                    borderColorStart,
                    borderColorStart,
                    300);
                drawGradientRect(
                    tooltipX - 3,
                    tooltipY + tooltipTextHeight + 2,
                    tooltipX + tooltipTextWidth + 3,
                    tooltipY + tooltipTextHeight + 3,
                    borderColorEnd,
                    borderColorEnd,
                    300);
                MinecraftForge.EVENT_BUS.post(
                    new RenderTooltipEvent.PostBackground(
                        stack,
                        textLines,
                        tooltipX,
                        tooltipY,
                        font,
                        tooltipTextWidth,
                        tooltipTextHeight));
                int textY = tooltipY;

                for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                    String line = textLines.get(lineNumber);
                    ITooltipLine handler = TooltipLineHandler.getTipLine(line);
                    if (handler != null) {
                        int width = getTooltipLineWidth(handler, font, tooltipTextWidth);
                        int height = Math.max(0, handler.getHeight(font, width));
                        handler.draw(font, tooltipX, textY, mouseX, mouseY, width, height);
                        textY += height;
                    } else {
                        String cleanText = line.replace(TooltipLineHandler.TOOLTIP_LINESPACE, "");
                        font.drawStringWithShadow(cleanText, tooltipX, textY, -1);
                        textY += 10;
                    }

                    if (lineNumber + 1 == titleLinesCount && lineNumber + 1 < textLines.size()) {
                        textY += 2;
                    }

                    if (lineNumber + 1 < textLines.size() && line.contains(TooltipLineHandler.TOOLTIP_LINESPACE)) {
                        textY += 2;
                    }
                }

                MinecraftForge.EVENT_BUS.post(
                    new RenderTooltipEvent.PostText(
                        stack,
                        textLines,
                        tooltipX,
                        tooltipY,
                        font,
                        tooltipTextWidth,
                        tooltipTextHeight));
                TooltipLineHandler.clear();
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.enableRescaleNormal();
            }
        }
    }

    /**
     * Resolves the actual width allocated to a tooltip component.
     *
     * @param handler      the tooltip component
     * @param font         the font renderer
     * @param tooltipWidth the available tooltip width
     * @return the allocated component width
     */
    private static int getTooltipLineWidth(ITooltipLine handler, FontRenderer font, int tooltipWidth) {
        int width = handler.getWidth(font);
        if (width < 0) return tooltipWidth;
        return Math.min(width, tooltipWidth);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor,
        int zLevel) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
        float startBlue = (float) (startColor & 255) / 255.0F;
        float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
        float endRed = (float) (endColor >> 16 & 255) / 255.0F;
        float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
        float endBlue = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = TessellatorManager.get();

        tessellator.startDrawingQuads();

        tessellator.setColorRGBA_F(startRed, startGreen, startBlue, startAlpha);
        tessellator.addVertex(right, top, zLevel);

        tessellator.setColorRGBA_F(startRed, startGreen, startBlue, startAlpha);
        tessellator.addVertex(left, top, zLevel);

        tessellator.setColorRGBA_F(endRed, endGreen, endBlue, endAlpha);
        tessellator.addVertex(left, bottom, zLevel);

        tessellator.setColorRGBA_F(endRed, endGreen, endBlue, endAlpha);
        tessellator.addVertex(right, bottom, zLevel);

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private static Field cachedSlotField = null;

    public static Slot getSlotUnderMouse(GuiContainer guiContainer) {
        if (guiContainer == null) {
            return null;
        }

        try {
            if (cachedSlotField == null) {
                cachedSlotField = ReflectionHelper.findField(GuiContainer.class, "theSlot", "field_147006_u");
                cachedSlotField.setAccessible(true);
            }
            return (Slot) cachedSlotField.get(guiContainer);
        } catch (Exception e) {
            return null;
        }
    }

    public static void drawIcon(int x, int y, IIcon icon, int width, int height) {
        if (icon == null) return;

        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        maxV = minV + (maxV - minV) * ((double) height / 16.0D);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, 0, minU, maxV);
        tessellator.addVertexWithUV(x + width, y + height, 0, maxU, maxV);
        tessellator.addVertexWithUV(x + width, y, 0, maxU, minV);
        tessellator.addVertexWithUV(x, y, 0, minU, minV);
        tessellator.draw();
    }

    /**
     * Render a fluid tank in a gui.
     *
     * @param gui        The gui to render in.
     * @param fluidStack The fluid to render.
     * @param capacity   The tank capacity.
     * @param x          The gui x position, including gui left.
     * @param y          The gui y position, including gui top.
     * @param width      The tank width.
     * @param height     The tank height.
     */
    public static void renderFluidTank(Gui gui, FluidStack fluidStack, int capacity, int x, int y, int width,
        int height) {
        if (fluidStack != null && fluidStack.getFluid() != null && capacity > 0) {
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderHelper.enableGUIStandardItemLighting();

            GL11.glEnable(GL11.GL_DEPTH_TEST);

            int level = (int) (height * (((double) fluidStack.amount) / capacity));
            IIcon icon = RenderHelpers.getFluidIcon(fluidStack, ForgeDirection.UP);
            int verticalOffset = 0;

            // Bind Texture Terrain (Blocks & Liquids)
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(TextureMap.locationBlocksTexture);

            // Fluids can have a custom overlay color
            Triple<Float, Float, Float> colorParts = Helpers.intToRGB(
                fluidStack.getFluid()
                    .getColor(fluidStack));
            GL11.glColor4f(colorParts.getLeft(), colorParts.getMiddle(), colorParts.getRight(), 1.0F);

            while (level > 0) {
                int textureHeight;
                if (level > 16) {
                    textureHeight = 16;
                    level -= 16;
                } else {
                    textureHeight = level;
                    level = 0;
                }

                drawIcon(x, y - textureHeight - verticalOffset + height, icon, width, textureHeight);
                verticalOffset += 16;
            }

            // Reset color & GL States when done
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        }
    }

    /**
     * Render the given fluid in a standard slot.
     *
     * @param gui        The gui to render in.
     * @param fluidStack The fluid to render.
     * @param x          The slot X position.
     * @param y          The slot Y position.
     */
    public static void renderFluidSlot(Gui gui, @Nullable FluidStack fluidStack, int x, int y) {
        if (fluidStack != null) {
            GuiHelpers.renderFluidTank(gui, fluidStack, fluidStack.amount, x, y, SLOT_SIZE_INNER, SLOT_SIZE_INNER);
        }
    }

    /**
     * Render a fluid tank in a gui with a tank overlay.
     * This assumes that the tank overlay has the provided width and height.
     *
     * @param gui             The gui to render in.
     * @param fluidStack      The fluid to render.
     * @param capacity        The tank capacity.
     * @param x               The gui x position, including gui left.
     * @param y               The gui y position, including gui top.
     * @param width           The tank width.
     * @param height          The tank height.
     * @param textureOverlay  The texture of the tank overlay.
     * @param overlayTextureX The overlay x texture position.
     * @param overlayTextureY The overlay y texture position.
     */
    public static void renderOverlayedFluidTank(Gui gui, @Nullable FluidStack fluidStack, int capacity, int x, int y,
        int width, int height, ResourceLocation textureOverlay, int overlayTextureX, int overlayTextureY) {
        renderFluidTank(gui, fluidStack, capacity, x, y, width, height);
        if (fluidStack != null && capacity > 0) {
            GlStateManager.enableBlend();
            RenderHelpers.bindTexture(textureOverlay);
            gui.drawTexturedModalRect(x, y, overlayTextureX, overlayTextureY, width, height);
        }
    }

    /**
     * Render a progress bar in a certain direction.
     * The currently bound texture will be used to render the progress bar.
     *
     * @param gui         The gui to render in.
     * @param x           The gui x position, including gui left.
     * @param y           The gui y position, including gui top.
     * @param width       The progress bar width.
     * @param height      The progress bar height.
     * @param textureX    The texture x position.
     * @param textureY    The texture y position.
     * @param direction   The direction to progress in.
     * @param progress    The current progress.
     * @param progressMax The maximum progress.
     */
    public static void renderProgressBar(Gui gui, int x, int y, int width, int height, int textureX, int textureY,
        ProgressDirection direction, int progress, int progressMax) {
        if (progressMax > 0 && progress > 0) {
            int scaledWidth = width;
            int scaledHeight = height;

            // Scale the width and/or height
            if (direction.getIncrementX() != 0) {
                scaledWidth = (int) (scaledWidth * (((double) progress) / progressMax));
            }
            if (direction.getIncrementY() != 0) {
                scaledHeight = (int) (scaledHeight * (((double) progress) / progressMax));
            }

            // If increments happen inversely, make sure we start incrementing from the other end of the progress bar
            if (direction.getIncrementX() < 0) {
                int offset = width - scaledWidth;
                x += offset;
                textureX += offset;
            }
            if (direction.getIncrementY() < 0) {
                int offset = height - scaledHeight;
                y += offset;
                textureY += offset;
            }

            gui.drawTexturedModalRect(x, y, textureX, textureY, scaledWidth, scaledHeight);
        }
    }

    /**
     * Draw a tooltip.
     *
     * @param gui   The gui to draw in.
     * @param lines A list of lines.
     * @param x     Tooltip X.
     * @param y     Tooltip Y.
     */
    public static void drawTooltip(GuiContainer gui, List<String> lines, int x, int y) {
        drawTooltip(gui, null, lines, x, y);
    }

    public static void drawTooltip(GuiContainer gui, @Nullable ItemStack stack, List<String> lines, int x, int y) {
        if (gui == null || lines == null || lines.isEmpty()) return;
        drawHoveringText(stack, lines, x, y, gui.width, gui.height, -1, Minecraft.getMinecraft().fontRenderer);
    }

    /**
     * Draw the background box for a tooltip.
     *
     * @param xStart        X
     * @param yStart        Y
     * @param tooltipWidth  Width
     * @param tooltipHeight Height
     */
    public static void drawTooltipBackground(int xStart, int yStart, int tooltipWidth, int tooltipHeight) {
        float zLevel = 300.0F;
        int color1 = -267386864;
        fillGradient(xStart - 3, yStart - 4, xStart + tooltipWidth + 3, yStart - 3, color1, color1, zLevel);
        fillGradient(
            xStart - 3,
            yStart + tooltipHeight + 3,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 4,
            color1,
            color1,
            zLevel);
        fillGradient(
            xStart - 3,
            yStart - 3,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3,
            color1,
            color1,
            zLevel);
        fillGradient(xStart - 4, yStart - 3, xStart - 3, yStart + tooltipHeight + 3, color1, color1, zLevel);
        fillGradient(
            xStart + tooltipWidth + 3,
            yStart - 3,
            xStart + tooltipWidth + 4,
            yStart + tooltipHeight + 3,
            color1,
            color1,
            zLevel);
        int color2 = 1347420415;
        int color3 = (color2 & 0xFEFEFE) >> 1 | color2 & 0xFF000000;
        fillGradient(
            xStart - 3,
            yStart - 3 + 1,
            xStart - 3 + 1,
            yStart + tooltipHeight + 3 - 1,
            color2,
            color3,
            zLevel);
        fillGradient(
            xStart + tooltipWidth + 2,
            yStart - 3 + 1,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3 - 1,
            color2,
            color3,
            zLevel);
        fillGradient(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart - 3 + 1, color2, color2, zLevel);
        fillGradient(
            xStart - 3,
            yStart + tooltipHeight + 2,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3,
            color3,
            color3,
            zLevel);
    }

    /**
     * Render a rectangle.
     */
    public static void fillGradient(int left, int top, int right, int bottom, int startColor, int endColor,
        float zLevel) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;

        GlStateManager.disableTexture();
        GlStateManager.enableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.instance;
        tessellator.startDrawingQuads();

        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double) right, (double) top, (double) zLevel);

        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex((double) left, (double) top, (double) zLevel);

        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double) left, (double) bottom, (double) zLevel);

        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex((double) right, (double) bottom, (double) zLevel);

        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
        GlStateManager.enableTexture();
    }

    /**
     * Render a tooltip if the mouse if in the bounding box defined by the given position, width and height.
     * The tooltip lines supplier can return an optional list.
     *
     * @param gui           The gui to render in.
     * @param x             The gui x position, excluding gui left.
     * @param y             The gui y position, excluding gui top.
     * @param width         The area width.
     * @param height        The area height.
     * @param mouseX        The mouse x position.
     * @param mouseY        The mouse y position.
     * @param linesSupplier A supplier for the optional tooltip lines to render.
     *                      No tooltip will be rendered when the optional value is absent.
     *                      This will only be called when needed.
     */
    public static void renderTooltipOptional(GuiContainer gui, int x, int y, int width, int height, int mouseX,
        int mouseY, Supplier<Optional<List<String>>> linesSupplier) {
        if (RenderHelpers.isPointInRegion(x, y, width, height, mouseX - gui.guiLeft, mouseY - gui.guiTop)) {
            linesSupplier.get()
                .ifPresent(lines -> drawTooltip(gui, lines, mouseX - gui.guiLeft, mouseY - gui.guiTop));
        }
    }

    /**
     * Render a tooltip if the mouse if in the bounding box defined by the given position, width and height.
     *
     * @param gui           The gui to render in.
     * @param x             The gui x position, excluding gui left.
     * @param y             The gui y position, excluding gui top.
     * @param width         The area width.
     * @param height        The area height.
     * @param mouseX        The mouse x position.
     * @param mouseY        The mouse y position.
     * @param linesSupplier A supplier for the tooltip lines to render.
     *                      This will only be called when needed.
     */
    public static void renderTooltip(GuiContainer gui, int x, int y, int width, int height, int mouseX, int mouseY,
        Supplier<List<String>> linesSupplier) {
        renderTooltipOptional(gui, x, y, width, height, mouseX, mouseY, () -> Optional.of(linesSupplier.get()));
    }

    private static final List<Pair<Long, String>> COUNT_SCALES = Lists.newArrayList(
        Pair.of(1000000000000000000L, "E"),
        Pair.of(1000000000000000L, "P"),
        Pair.of(1000000000000L, "T"),
        Pair.of(1000000000L, "G"),
        Pair.of(1000000L, "M"),
        Pair.of(1000L, "K"));

    /**
     * Stringify a (potentially large) quantity to a scaled string.
     *
     * For example, 123765 will be converted as 1.23M.
     *
     * @param quantity A quantity.
     * @return A scaled quantity string.
     */
    public static String quantityToScaledString(long quantity) {
        for (Pair<Long, String> countScale : COUNT_SCALES) {
            long scale = countScale.getLeft();
            if (quantity >= scale) {
                long division = quantity / scale;
                String divisionString = String.valueOf(division);

                // Add digits if string is short
                if (division < 10) {
                    long mod = quantity % scale;
                    if (mod > 0) {
                        long digits = mod * 100 / scale;
                        divisionString += "." + (digits < 10 ? "0" : "") + String.valueOf(digits);
                    }
                } else if (division < 100) {
                    long mod = quantity % scale;
                    if (mod > 0) {
                        long digits = mod * 10 / scale;
                        divisionString += "." + String.valueOf(digits);
                    }
                }

                return divisionString + countScale.getRight();
            }
        }
        return String.valueOf(quantity);
    }

    /**
     * Represents the direction of a progress bar.
     */
    public static enum ProgressDirection {

        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0),

        UP_LEFT(-1, -1),
        UP_RIGHT(1, -1),
        DOWN_LEFT(-1, 1),
        DOWN_RIGHT(1, 1);

        private final int incrementX;
        private final int incrementY;

        private ProgressDirection(int incrementX, int incrementY) {
            this.incrementX = incrementX;
            this.incrementY = incrementY;
        }

        public int getIncrementX() {
            return incrementX;
        }

        public int getIncrementY() {
            return incrementY;
        }
    }
}
