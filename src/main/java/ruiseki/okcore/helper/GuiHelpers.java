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

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import cpw.mods.fml.relauncher.ReflectionHelper;
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
     * Use this version if calling from somewhere where ItemStack context is available.
     *
     * @see #drawHoveringText(List, int, int, int, int, int, FontRenderer)
     */
    public static void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY,
        int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
        if (!textLines.isEmpty()) {
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
                return;
            }
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

            for (String textLine : textLines) {
                int textLineWidth = font.getStringWidth(textLine);

                if (textLineWidth > tooltipTextWidth) {
                    tooltipTextWidth = textLineWidth;
                }
            }

            boolean needsWrap = false;

            int titleLinesCount = 1;
            int tooltipX = mouseX + 12;
            if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
                tooltipX = mouseX - 16 - tooltipTextWidth;
                if (tooltipX < 4) // if the tooltip doesn't fit on the screen
                {
                    if (mouseX > screenWidth / 2) {
                        tooltipTextWidth = mouseX - 12 - 8;
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
                List<String> wrappedTextLines = new ArrayList<String>();
                for (int i = 0; i < textLines.size(); i++) {
                    String textLine = textLines.get(i);
                    List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                    if (i == 0) {
                        titleLinesCount = wrappedLine.size();
                    }

                    for (String line : wrappedLine) {
                        int lineWidth = font.getStringWidth(line);
                        if (lineWidth > wrappedTooltipWidth) {
                            wrappedTooltipWidth = lineWidth;
                        }
                        wrappedTextLines.add(line);
                    }
                }
                tooltipTextWidth = wrappedTooltipWidth;
                textLines = wrappedTextLines;

                if (mouseX > screenWidth / 2) {
                    tooltipX = mouseX - 16 - tooltipTextWidth;
                } else {
                    tooltipX = mouseX + 12;
                }
            }

            int tooltipY = mouseY - 12;
            int tooltipHeight = 8;

            if (textLines.size() > 1) {
                tooltipHeight += (textLines.size() - 1) * 10;
                if (textLines.size() > titleLinesCount) {
                    tooltipHeight += 2; // gap between title lines and next lines
                }
            }

            if (tooltipY < 4) {
                tooltipY = 4;
            } else if (tooltipY + tooltipHeight + 4 > screenHeight) {
                tooltipY = screenHeight - tooltipHeight - 4;
            }

            final int zLevel = 300;
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
                zLevel);
            drawGradientRect(
                tooltipX - 3,
                tooltipY + tooltipHeight + 3,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 4,
                backgroundColor,
                backgroundColor,
                zLevel);
            drawGradientRect(
                tooltipX - 3,
                tooltipY - 3,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor,
                zLevel);
            drawGradientRect(
                tooltipX - 4,
                tooltipY - 3,
                tooltipX - 3,
                tooltipY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor,
                zLevel);
            drawGradientRect(
                tooltipX + tooltipTextWidth + 3,
                tooltipY - 3,
                tooltipX + tooltipTextWidth + 4,
                tooltipY + tooltipHeight + 3,
                backgroundColor,
                backgroundColor,
                zLevel);
            drawGradientRect(
                tooltipX - 3,
                tooltipY - 3 + 1,
                tooltipX - 3 + 1,
                tooltipY + tooltipHeight + 3 - 1,
                borderColorStart,
                borderColorEnd,
                zLevel);
            drawGradientRect(
                tooltipX + tooltipTextWidth + 2,
                tooltipY - 3 + 1,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 3 - 1,
                borderColorStart,
                borderColorEnd,
                zLevel);
            drawGradientRect(
                tooltipX - 3,
                tooltipY - 3,
                tooltipX + tooltipTextWidth + 3,
                tooltipY - 3 + 1,
                borderColorStart,
                borderColorStart,
                zLevel);
            drawGradientRect(
                tooltipX - 3,
                tooltipY + tooltipHeight + 2,
                tooltipX + tooltipTextWidth + 3,
                tooltipY + tooltipHeight + 3,
                borderColorEnd,
                borderColorEnd,
                zLevel);

            MinecraftForge.EVENT_BUS.post(
                new RenderTooltipEvent.PostBackground(
                    stack,
                    textLines,
                    tooltipX,
                    tooltipY,
                    font,
                    tooltipTextWidth,
                    tooltipHeight));
            int tooltipTop = tooltipY;

            for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
                String line = textLines.get(lineNumber);
                font.drawStringWithShadow(line, tooltipX, tooltipY, -1);

                if (lineNumber + 1 == titleLinesCount) {
                    tooltipY += 2;
                }

                tooltipY += 10;
            }

            MinecraftForge.EVENT_BUS.post(
                new RenderTooltipEvent.PostText(
                    stack,
                    textLines,
                    tooltipX,
                    tooltipTop,
                    font,
                    tooltipTextWidth,
                    tooltipHeight));

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
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
        if (lines == null || lines.isEmpty()) {
            return;
        }

        int guiLeft = gui.guiLeft;
        int guiTop = gui.guiTop;
        int width = gui.width;
        int height = gui.height;
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();

        int tooltipWidth = 0;
        for (String line : lines) {
            int tempWidth = mc.fontRenderer.getStringWidth(line);
            if (tempWidth > tooltipWidth) {
                tooltipWidth = tempWidth;
            }
        }

        int xStart = x + 12;
        int yStart = y - 12;
        int tooltipHeight = 8;

        if (lines.size() > 1) {
            tooltipHeight += 2 + (lines.size() - 1) * 10;
        }

        if (guiLeft + xStart + tooltipWidth + 6 > width) {
            xStart = width - tooltipWidth - guiLeft - 6;
        }

        if (guiTop + yStart + tooltipHeight + 6 > height) {
            yStart = height - tooltipHeight - guiTop - 6;
        }

        final int zLevel = 300;
        render.zLevel = 300.0F;

        int color1 = 0xF0100010; // -267386864 in HEX
        drawGradientRect(xStart - 3, yStart - 4, xStart + tooltipWidth + 3, yStart - 3, color1, color1, zLevel);
        drawGradientRect(
            xStart - 3,
            yStart + tooltipHeight + 3,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 4,
            color1,
            color1,
            zLevel);
        drawGradientRect(
            xStart - 3,
            yStart - 3,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3,
            color1,
            color1,
            zLevel);
        drawGradientRect(xStart - 4, yStart - 3, xStart - 3, yStart + tooltipHeight + 3, color1, color1, zLevel);
        drawGradientRect(
            xStart + tooltipWidth + 3,
            yStart - 3,
            xStart + tooltipWidth + 4,
            yStart + tooltipHeight + 3,
            color1,
            color1,
            zLevel);

        int color2 = 0x505000FF; // 1347420415 in HEX
        int color3 = (color2 & 0xFEFEFE) >> 1 | color2 & 0xFF000000;
        drawGradientRect(
            xStart - 3,
            yStart - 3 + 1,
            xStart - 3 + 1,
            yStart + tooltipHeight + 3 - 1,
            color2,
            color3,
            zLevel);
        drawGradientRect(
            xStart + tooltipWidth + 2,
            yStart - 3 + 1,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3 - 1,
            color2,
            color3,
            zLevel);
        drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart - 3 + 1, color2, color2, zLevel);
        drawGradientRect(
            xStart - 3,
            yStart + tooltipHeight + 2,
            xStart + tooltipWidth + 3,
            yStart + tooltipHeight + 3,
            color3,
            color3,
            zLevel);

        for (int stringIndex = 0; stringIndex < lines.size(); ++stringIndex) {
            String line = lines.get(stringIndex);

            if (stringIndex == 0) {
                line = EnumChatFormatting.WHITE + line;
            } else {
                line = EnumChatFormatting.GRAY + line;
            }

            mc.fontRenderer.drawStringWithShadow(line, xStart, yStart, -1);

            if (stringIndex == 0) {
                yStart += 2;
            }

            yStart += 10;
        }

        GlStateManager.popMatrix();
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        render.zLevel = 0.0F;
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
