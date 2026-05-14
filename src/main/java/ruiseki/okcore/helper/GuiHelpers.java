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
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.event.guide.BookEvent;
import ruiseki.okcore.guide.gui.GuiEntry;
import ruiseki.okcore.guide.impl.Book;
import ruiseki.okcore.guide.impl.abstraction.CategoryAbstract;
import ruiseki.okcore.guide.impl.abstraction.EntryAbstract;

public class GuiHelpers {

    private static final RenderItem render = new RenderItem();

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

    @SideOnly(Side.CLIENT)
    public static void openBookClient(Book book, CategoryAbstract category, EntryAbstract entryAbstract,
        EntityPlayer player) {
        BookEvent.Open event = new BookEvent.Open(book, player);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            player.addChatComponentMessage(event.getCanceledText());
            return;
        }
        Minecraft.getMinecraft()
            .displayGuiScreen(new GuiEntry(book, category, entryAbstract, player));
    }
}
