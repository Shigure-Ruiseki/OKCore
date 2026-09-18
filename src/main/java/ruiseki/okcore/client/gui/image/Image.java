package ruiseki.okcore.client.gui.image;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import lombok.Data;
import ruiseki.okcore.client.renderer.GlStateManager;

/**
 * A wrapper that contains a reference to a {@link net.minecraft.util.ResourceLocation} and its sheet position.
 *
 * @author rubensworks
 */
@Data
public class Image implements IImage {

    private final ResourceLocation resourceLocation;
    private final int sheetX, sheetY, sheetWidth, sheetHeight;
    private final int textureWidth, textureHeight;

    public Image(ResourceLocation resourceLocation, int sheetX, int sheetY, int sheetWidth, int sheetHeight) {
        this(resourceLocation, sheetX, sheetY, sheetWidth, sheetHeight, 256, 256);
    }

    public Image(ResourceLocation resourceLocation, int sheetX, int sheetY, int sheetWidth, int sheetHeight,
        int textureWidth, int textureHeight) {
        this.resourceLocation = resourceLocation;
        this.sheetX = sheetX;
        this.sheetY = sheetY;
        this.sheetWidth = sheetWidth;
        this.sheetHeight = sheetHeight;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public void draw(Gui gui, int x, int y) {
        drawWithColor(gui, x, y, this.sheetWidth, this.sheetHeight, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void draw(Gui gui, int x, int y, int width, int height) {
        drawWithColor(gui, x, y, width, height, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void drawWithColor(Gui gui, int x, int y, float r, float g, float b, float a) {
        drawWithColor(gui, x, y, this.sheetWidth, this.sheetHeight, r, g, b, a);
    }

    @Override
    public void drawWithColor(Gui gui, int x, int y, int width, int height, float r, float g, float b, float a) {
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.resourceLocation);

        float f = 1.0F / (float) this.textureWidth;
        float f1 = 1.0F / (float) this.textureHeight;

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r, g, b, a);
        tessellator.addVertexWithUV(
            x,
            y + height,
            0.0D,
            (float) this.sheetX * f,
            (float) (this.sheetY + this.sheetHeight) * f1);
        tessellator.addVertexWithUV(
            x + width,
            y + height,
            0.0D,
            (float) (this.sheetX + this.sheetWidth) * f,
            (float) (this.sheetY + this.sheetHeight) * f1);
        tessellator
            .addVertexWithUV(x + width, y, 0.0D, (float) (this.sheetX + this.sheetWidth) * f, (float) this.sheetY * f1);
        tessellator.addVertexWithUV(x, y, 0.0D, (float) this.sheetX * f, (float) this.sheetY * f1);
        tessellator.draw();

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    @Override
    public void drawWorldWithAlpha(TextureManager textureManager, float x1, float x2, float y1, float y2, float z,
        float alpha) {
        GlStateManager.pushMatrix();

        textureManager.bindTexture(getResourceLocation());

        float u1 = (float) getSheetX() / (float) this.textureWidth;
        float u2 = (float) (getSheetX() + getSheetWidth()) / (float) this.textureWidth;
        float v1 = (float) getSheetY() / (float) this.textureHeight;
        float v2 = (float) (getSheetY() + getSheetHeight()) / (float) this.textureHeight;

        int a = Math.round(alpha * 255.0F);

        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, a);

        tessellator.addVertexWithUV(x2, y2, z, u2, v2);
        tessellator.addVertexWithUV(x2, y1, z, u2, v1);
        tessellator.addVertexWithUV(x1, y1, z, u1, v1);
        tessellator.addVertexWithUV(x1, y2, z, u1, v2);

        tessellator.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public int getWidth() {
        return this.sheetWidth;
    }

    @Override
    public int getHeight() {
        return this.sheetHeight;
    }
}
