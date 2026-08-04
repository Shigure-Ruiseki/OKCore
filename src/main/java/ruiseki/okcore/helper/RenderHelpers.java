package ruiseki.okcore.helper;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * A helper for rendering.
 *
 * @author rubensworks
 * @modifier okcore
 */
@SideOnly(Side.CLIENT)
public class RenderHelpers {

    private static final Random rand = new Random();

    /**
     * Bind a texture to the rendering engine.
     */
    public static void bindTexture(ResourceLocation texture) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(texture);
    }

    public static void drawScaledCenteredString(FontRenderer fontRenderer, String string, int x, int y, int maxWidth,
        int color) {
        drawScaledCenteredString(fontRenderer, string, x, y, maxWidth, 1.0F, maxWidth, color);
    }

    public static void drawScaledCenteredString(FontRenderer fontRenderer, String string, int x, int y, int width,
        float originalScale, int maxWidth, int color) {
        float originalWidth = fontRenderer.getStringWidth(string) * originalScale;
        float scale = Math.min(originalScale, maxWidth / originalWidth * originalScale);
        drawScaledCenteredString(fontRenderer, string, x, y, width, scale, color);
    }

    public static void drawScaledCenteredString(FontRenderer fontRenderer, String string, int x, int y, int width,
        float scale, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, 1.0f);
        int titleLength = fontRenderer.getStringWidth(string);
        int titleHeight = fontRenderer.FONT_HEIGHT;
        fontRenderer.drawString(
            string,
            Math.round((x + width / 2) / scale - titleLength / 2),
            Math.round(y / scale - titleHeight / 2),
            color);
        GL11.glPopMatrix();
    }

    /**
     * A custom way of spawning block hit effects .
     */
    public static void addBlockHitEffects(World world, Block block, int meta, int x, int y, int z,
        ForgeDirection side) {
        if (block.getRenderType() != -1) {
            float f = 0.1F;
            double d0 = (double) x
                + rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - (double) (f * 2.0F))
                + (double) f
                + block.getBlockBoundsMinX();
            double d1 = (double) y
                + rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - (double) (f * 2.0F))
                + (double) f
                + block.getBlockBoundsMinY();
            double d2 = (double) z
                + rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - (double) (f * 2.0F))
                + (double) f
                + block.getBlockBoundsMinZ();

            if (side == ForgeDirection.DOWN) d1 = (double) y + block.getBlockBoundsMinY() - (double) f;
            if (side == ForgeDirection.UP) d1 = (double) y + block.getBlockBoundsMaxY() + (double) f;
            if (side == ForgeDirection.NORTH) d2 = (double) z + block.getBlockBoundsMinZ() - (double) f;
            if (side == ForgeDirection.SOUTH) d2 = (double) z + block.getBlockBoundsMaxZ() + (double) f;
            if (side == ForgeDirection.WEST) d0 = (double) x + block.getBlockBoundsMinX() - (double) f;
            if (side == ForgeDirection.EAST) d0 = (double) x + block.getBlockBoundsMaxX() + (double) f;

            EntityFX fx = new EntityDiggingFX(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, block, meta);
            Minecraft.getMinecraft().effectRenderer.addEffect(fx);
        }
    }

    /**
     * Render the given item in the 3D world
     */
    public static void renderItem(World world, ItemStack itemStack, double x, double y, double z) {
        if (itemStack != null) {
            GL11.glPushMatrix();
            EntityItem entityitem = new EntityItem(world, 0.0D, 0.0D, 0.0D, itemStack);
            entityitem.hoverStart = 0.0F;

            RenderItem.renderInFrame = true;
            RenderManager.instance.renderEntityWithPosYaw(entityitem, x, y, z, 0.0F, 0.0F);
            RenderItem.renderInFrame = false;
            GL11.glPopMatrix();
        }
    }

    /**
     * Get the default icon from a block.
     */
    public static IIcon getBlockIcon(Block block) {
        // 2 is usually the North/Front side, 0 is default metadata
        return block.getIcon(2, 0);
    }

    /**
     * Get the icon of a fluid.
     */
    public static IIcon getFluidIcon(Fluid fluid, ForgeDirection side) {
        return getFluidIcon(new FluidStack(fluid, 1000), side);
    }

    /**
     * Get the icon of a fluid.
     */
    public static IIcon getFluidIcon(FluidStack fluid, ForgeDirection side) {
        if (fluid == null || fluid.getFluid() == null) return Blocks.water.getIcon(0, 0);

        IIcon icon = fluid.getFluid()
            .getIcon(fluid);
        if (icon == null) {
            icon = fluid.getFluid()
                .getIcon();
        }

        if (icon == null) {
            Block block = fluid.getFluid()
                .getBlock();
            if (block != null) {
                icon = block.getIcon(0, 0);
            } else {
                icon = Blocks.water.getIcon(0, 0);
            }
        }
        return icon;
    }

    /**
     * Prepare a GL context for rendering fluids.
     */
    public static void renderFluidContext(FluidStack fluid, double x, double y, double z, IFluidContextRender render) {
        if (fluid != null && fluid.amount > 0) {
            GL11.glPushMatrix();

            // Make sure both sides are rendered
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);

            // Correct color & lighting
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Set to current relative player location
            GL11.glTranslated(x, y, z);

            // Bind block texture map
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(TextureMap.locationBlocksTexture);

            render.renderFluid(fluid);

            // Reset states
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPopMatrix();
        }
    }

    public static void renderTileFluidContext(final FluidStack fluid, final double x, final double y, final double z,
        final TileEntity tile, final IFluidContextRender render) {
        renderFluidContext(fluid, x, y, z, render);
    }

    /**
     * Get the fluid color.
     */
    public static Triple<Float, Float, Float> getFluidVertexBufferColor(FluidStack fluidStack) {
        int color = fluidStack.getFluid()
            .getColor(fluidStack);
        return Helpers.intToRGB(color);
    }

    /**
     * Get fluid color as Integer.
     */
    public static int getFluidColor(FluidStack fluidStack) {
        Triple<Float, Float, Float> colorParts = Helpers.intToRGB(
            fluidStack.getFluid()
                .getColor(fluidStack));
        return Helpers.RGBAToInt(
            (int) (colorParts.getRight() * 255),
            (int) (colorParts.getMiddle() * 255),
            (int) (colorParts.getLeft() * 255),
            255);
    }

    /**
     * Check if a point is inside a region.
     * @param left Left-top corner x
     * @param top Left-top corner y
     * @param width The width
     * @param height The height
     * @param pointX The point x
     * @param pointY The point y
     * @return If the point is inside the region.
     */
    public static boolean isPointInRegion(int left, int top, int width, int height, int pointX, int pointY) {
        return pointX >= left && pointX < left + width && pointY >= top && pointY < top + height;
    }

    /**
     * Check if a point is inside a region.
     * @param region The region.
     * @param point The point.
     * @return If the point is inside the region.
     */
    public static boolean isPointInRegion(Rectangle region, Point point) {
        return isPointInRegion(region.x, region.y, region.width, region.height, point.x, point.y);
    }

    /**
     * Check if a point is inside a button's region.
     * @param button The button.
     * @param pointX The point x
     * @param pointY The point y
     * @return If the point is inside the button's region.
     */
    public static boolean isPointInButton(GuiButton button, int pointX, int pointY) {
        return isPointInRegion(button.xPosition, button.yPosition, button.width, button.height, pointX, pointY);
    }

    /**
     * Runnable for {@link RenderHelpers#renderFluidContext(FluidStack, double, double, double, IFluidContextRender)}.
     * @author rubensworks
     */
    public static interface IFluidContextRender {

        /**
         * Render the fluid.
         * @param fluid The fluid stack.
         */
        public void renderFluid(FluidStack fluid);
    }
}
