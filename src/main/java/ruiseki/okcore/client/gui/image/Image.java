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

    public Image(ResourceLocation resourceLocation, int sheetX, int sheetY, int sheetWidth, int sheetHeight) {
        this.resourceLocation = resourceLocation;
        this.sheetX = sheetX;
        this.sheetY = sheetY;
        this.sheetWidth = sheetWidth;
        this.sheetHeight = sheetHeight;
    }

    @Override
    public void draw(Gui gui, int x, int y) {
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
        gui.drawTexturedModalRect(x, y, sheetX, sheetY, sheetWidth, sheetHeight);
        GlStateManager.disableBlend();
    }

    @Override
    public void drawWithColor(Gui gui, int x, int y, float r, float g, float b, float a) {
        GlStateManager.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, a);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.resourceLocation);
        gui.drawTexturedModalRect(x, y, this.sheetX, this.sheetY, this.sheetWidth, this.sheetHeight);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
    }

    @Override
    public void drawWorldWithAlpha(TextureManager textureManager, float x1, float x2, float y1, float y2, float z,
        float alpha) {
        GlStateManager.pushMatrix();

        textureManager.bindTexture(getResourceLocation());

        float u1 = (float) getSheetX() / 256.0F;
        float u2 = (float) (getSheetX() + getSheetWidth()) / 256.0F;
        float v1 = (float) getSheetY() / 256.0F;
        float v2 = (float) (getSheetY() + getSheetHeight()) / 256.0F;

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
