package ruiseki.okcore.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.helper.GuiHelpers;

/**
 * An item renderer that can handle stack sizes larger than 64.
 * 1.7.10 Backport Version.
 *
 * @author rubensworks
 */
@SideOnly(Side.CLIENT)
public class RenderItemExtendedSlotCount extends RenderItem {

    private static RenderItemExtendedSlotCount instance;

    protected RenderItemExtendedSlotCount() {
        super();
    }

    public static RenderItemExtendedSlotCount getInstance() {
        if (instance == null) initialize();
        return instance;
    }

    public static void initialize() {
        instance = new RenderItemExtendedSlotCount();
    }

    public static void drawSlotText(FontRenderer fontRenderer, String string, int x, int y) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPushMatrix();
        float scale = 0.5f;
        GL11.glScalef(scale, scale, 1.0f);

        int width = fontRenderer.getStringWidth(string);
        fontRenderer.drawStringWithShadow(string, (int) ((x + 16) / scale - width), (int) ((y + 12) / scale), 0xFFFFFF);

        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
    }

    @Override
    public void renderItemOverlayIntoGUI(FontRenderer fr, TextureManager tm, ItemStack stack, int xPosition,
        int yPosition, String text) {
        if (stack != null && stack.getItem() != null) {
            if (stack.stackSize != 1 || text != null) {
                String renderText = text == null ? GuiHelpers.quantityToScaledString(stack.stackSize) : text;
                drawSlotText(fr, renderText, xPosition, yPosition);
            }

            if (stack.getItem()
                .isDamaged(stack)) {
                int k = (int) Math
                    .round(13.0D - (double) stack.getItemDamageForDisplay() * 13.0D / (double) stack.getMaxDamage());
                int l = (int) Math
                    .round(255.0D - (double) stack.getItemDamageForDisplay() * 255.0D / (double) stack.getMaxDamage());

                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_ALPHA_TEST);
                GL11.glDisable(GL11.GL_BLEND);

                Tessellator tessellator = Tessellator.instance;
                int i1 = 255 - l << 16 | l << 8;
                int j1 = (255 - l) / 4 << 16 | 16128;

                this.renderQuad(tessellator, xPosition + 2, yPosition + 13, 13, 2, 0);
                this.renderQuad(tessellator, xPosition + 2, yPosition + 13, 12, 1, j1);
                this.renderQuad(tessellator, xPosition + 2, yPosition + 13, k, 1, i1);

                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_ALPHA_TEST);
            }
        }
    }

    private void renderQuad(Tessellator tessellator, int x, int y, int width, int height, int color) {
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(color);
        tessellator.addVertex((double) (x + 0), (double) (y + 0), 0.0D);
        tessellator.addVertex((double) (x + 0), (double) (y + height), 0.0D);
        tessellator.addVertex((double) (x + width), (double) (y + height), 0.0D);
        tessellator.addVertex((double) (x + width), (double) (y + 0), 0.0D);
        tessellator.draw();
    }
}
